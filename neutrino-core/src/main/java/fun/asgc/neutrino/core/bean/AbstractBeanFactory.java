/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.core.bean;

import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.context.LifeCycle;
import fun.asgc.neutrino.core.context.LifeCycleManager;
import fun.asgc.neutrino.core.context.LifeCycleStatus;
import fun.asgc.neutrino.core.exception.BeanException;
import fun.asgc.neutrino.core.util.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 抽象的bean工厂
 * @author: aoshiguchen
 * @date: 2022/7/1
 */
@Slf4j
public abstract class AbstractBeanFactory implements BeanFactory, BeanRegistry, LifeCycle {
	/**
	 * 父工厂
	 */
	protected AbstractBeanFactory parent;
	/**
	 * bean缓存
	 */
	protected Map<BeanIdentity, BeanWrapper> beanCache;
	/**
	 * 互斥锁
	 */
	private Object mutex;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 生命周期管理
	 */
	private LifeCycleManager lifeCycleManager = LifeCycleManager.create();
	/**
	 * 环境参数
	 */
	private static volatile Environment environment;
	/**
	 * 调度器
	 */
	private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

	public AbstractBeanFactory(String name) {
		this(null, name);
	}

	public AbstractBeanFactory(AbstractBeanFactory parent, String name) {
		Assert.notNull(name, "工厂名称不能为空");
		this.parent = parent;
		this.beanCache = new ConcurrentHashMap<>(256);
		this.name = name;
		if (null == parent) {
			this.mutex = new Object();
		} else {
			this.mutex = parent.getMutex();
		}
		this.registerBean(this, this.name);
	}

	@Override
	public <T> T getBean(String name, Object... args) throws BeanException {
		Assert.notEmpty(name, "Bean名称不能为空!");
		T bean = (null == parent) ? null : parent.getBean(name, args);
		return null != bean ? bean : doGetBean(name, args);
	}


	@Override
	public <T> T getBeanByName(Class<T> type, String name, Object... args) {
		Assert.notEmpty(name, "Bean名称不能为空!");
		T bean = (null == parent) ? null : parent.getBeanByName(type, name, args);
		return null != bean ? bean : doGetBeanByName(type, name, args);
	}

	@Override
	public <T> T getBean(Class<T> type, Object... args) throws BeanException {
		Assert.notNull(type, "Bean类型不能为空!");
		T bean = (null == parent) ? null : parent.getBean(type, args);
		return null != bean ? bean : doGetBean(type, args);
	}

	@Override
	public <T> T getBeanByTypeAndName(Class<T> type, String name, Object... args) throws BeanException {
		Assert.notEmpty(name, "Bean名称不能为空!");
		Assert.notNull(type, "Bean类型不能为空!");
		T bean = (null == parent) ? null : parent.getBeanByTypeAndName(type, name, args);
		return null != bean ? bean : doGetBeanByTypeAndName(type, name, args);
	}

	@Override
	public <T> T getBean(BeanIdentity identity, Object... args) throws BeanException {
		Assert.notNull(identity, "Bean的身份标识不能为空!");
		return (T)getBeanByTypeAndName(identity.getType(), identity.getName(), args);
	}

	@Override
	public <T> List<T> getBeanList(Class<T> type, Object... args) throws BeanException {
		return CollectionUtil.addAll(() -> new LinkedList<>(),
			null == parent ? null : parent.getBeanList(type, args),
			doGetBeanList(type, args)
		);
	}

	@Override
	public <T> List<T> getBeanListByName(Class<T> type, String name, Object... args) throws BeanException {
		return CollectionUtil.addAll(() -> new LinkedList<>(),
			null == parent ? null : parent.getBeanListByName(type, name, args),
			doGetBeanListByName(type, name, args)
		);
	}

	@Override
	public boolean hasBean(Class<?> type, String name) {
		boolean res = (null != parent) ? parent.hasBean(type, name) : false;
		return res || doHasBean(type, name);
	}

	@Override
	public boolean hasBean(Class<?> type) {
		return hasBean(type, ClassUtil.getBeanName(type));
	}

	@Override
	public int countBean(String name) {
		int count = (null != parent) ? parent.countBean(name) : 0;
		return count + doCountBean(name);
	}

	@Override
	public int countBean(Class<?> type) {
		int count = (null != parent) ? parent.countBean(type) : 0;
		return count + doCountBean(type);
	}

	@Override
	public void registerBean(Object obj) throws BeanException {
		Assert.notNull(obj, "被注册的bean实例不能为空!");
		String name = TypeUtil.getDefaultVariableName(obj.getClass());
		registerBean(obj, name);
	}

	/**
	 * 手动new出来的bean注册进来，视为已经初始化了
	 * @param obj
	 * @param name
	 * @throws BeanException
	 */
	@Override
	public void registerBean(Object obj, String name) throws BeanException {
		Assert.notNull(obj, "被注册的bean实例不能为空!");
		Assert.notNull(obj, "被注册的bean名称不能为空!");
		synchronized (mutex) {
			if (hasBean(obj.getClass(), name)) {
				throw new BeanException(String.format("Bean[type:%s name:%s] 已存在，不能重复注册!", obj.getClass().getName(), name));
			}
			addBean(new BeanWrapper()
				.setType(obj.getClass())
				.setName(name)
				.setInstance(obj)
				.setStatus(BeanStatus.INIT)
			);
		}
	}

	@Override
	public void registerBean(Class<?> type) throws BeanException {
		registerBean(type,  ClassUtil.getBeanName(type));
	}

	@Override
	public void registerBean(Class<?> type, String name) throws BeanException {
		synchronized (mutex) {
			if (hasBean(type, name)) {
				throw new BeanException(String.format("Bean[type:%s name:%s] 已存在，不能重复注册!", type.getName(), name));
			}
			addBean(type, name);
		}
	}

	private <T> T doGetBean(String name, Object... args) throws BeanException {
		List<BeanWrapper> beanList  = findBeanList(name);
		if (CollectionUtil.isEmpty(beanList)) {
			return null;
		} else if (beanList.size() > 1) {
			throw new BeanException(String.format("Bean[name:%s] 存在多个实例!", name));
		}
		return getOrNew(beanList.get(0), args);
	}

	private <T> T doGetBeanByName(Class<T> type, String name, Object... args) throws BeanException {
		List<BeanWrapper> beanList  = findBeanList(type, name);
		if (CollectionUtil.isEmpty(beanList)) {
			return null;
		} else if (beanList.size() > 1) {
			throw new BeanException(String.format("Bean[name:%s] 存在多个实例!", name));
		}
		return getOrNew(beanList.get(0), args);
	}

	private <T> T doGetBean(Class<T> type, Object... args) throws BeanException {
		List<BeanWrapper> beanList  = findBeanList(type);
		if (CollectionUtil.isEmpty(beanList)) {
			return null;
		} else if (beanList.size() > 1) {
			throw new BeanException(String.format("Bean[type:%s] 存在多个实例!", type));
		}
		return getOrNew(beanList.get(0), args);
	}

	private <T> T doGetBeanByTypeAndName(Class<T> type, String name, Object... args) throws BeanException {
		BeanWrapper bean = findBean(type, name);
		if (null == bean) {
			return null;
		}
		if (null == bean) {
			return null;
		}
		return getOrNew(bean, args);
	}

	private <T> List<T> doGetBeanList(Class<T> type, Object... args) throws BeanException {
		List<BeanWrapper> beanList  = findBeanList(type);
		if (CollectionUtil.isEmpty(beanList)) {
			return null;
		}
		return getOrNew(beanList, args);
	}

	private  <T> List<T> doGetBeanListByName(Class<T> type, String name, Object... args) throws BeanException {
		List<BeanWrapper> beanList  = findBeanList(type, name);
		if (CollectionUtil.isEmpty(beanList)) {
			return null;
		}
		return getOrNew(beanList, args);
	}

	private boolean doHasBean(Class<?> type, String name) {
		return beanCache.containsKey(new BeanIdentity(name, type));
	}

	private int doCountBean(String name) {
		return beanCache.keySet().stream().filter(e -> e.getName().equals(name)).collect(Collectors.counting()).intValue();
	}

	private int doCountBean(Class<?> type) {
		return beanCache.keySet().stream().filter(e -> type.isAssignableFrom(e.getType())).collect(Collectors.counting()).intValue();
	}

	public Object getMutex() {
		return mutex;
	}

	/**
	 * 查找bean
	 * @param name
	 * @return
	 */
	private List<BeanWrapper> findBeanList(String name) {
		Assert.notNull(name, "bean的名称不能为空!");
		List<BeanWrapper> beanList = new LinkedList<>();
		for (BeanIdentity identity : beanCache.keySet()) {
			if (identity.getName().equals(name)) {
				beanList.add(beanCache.get(identity));
			}
		}
		return beanList;
	}

	/**
	 * 查找bean
	 * @param type
	 * @return
	 */
	private List<BeanWrapper> findBeanList(Class<?> type) {
		Assert.notNull(type, "bean的类型不能为空!");
		List<BeanWrapper> beanList = new LinkedList<>();
		for (BeanIdentity identity : beanCache.keySet()) {
			if (type.isAssignableFrom(identity.getType())) {
				beanList.add(beanCache.get(identity));
			}
		}
		return beanList;
	}

	/**
	 * 查找bean
	 * @param type
	 * @return
	 */
	private List<BeanWrapper> findBeanList(Class<?> type, String name) {
		Assert.notNull(type, "bean的类型不能为空!");
		List<BeanWrapper> beanList = new LinkedList<>();
		for (BeanIdentity identity : beanCache.keySet()) {
			if (type.isAssignableFrom(identity.getType()) && identity.getName().equals(name)) {
				beanList.add(beanCache.get(identity));
			}
		}
		return beanList;
	}

	/**
	 * 查找bean
	 * @param type
	 * @param name
	 * @return
	 */
	private BeanWrapper findBean(Class<?> type, String name) {
		Assert.notNull(type, "bean的类型不能为空!");
		Assert.notNull(name, "bean的名称不能为空！");
		return beanCache.get(new BeanIdentity(name, type));
	}

	/**
	 * 新增一个bean
	 * @param bean
	 */
	protected void addBean(BeanWrapper bean) throws BeanException {
		beanCache.put(new BeanIdentity(bean.getName(), bean.getType()), bean);
		if (FactoryBean.class.isAssignableFrom(bean.getType())) {
			dependencyCheck(bean);
			newInstance(bean);
			// 工厂bean
			BeanIdentity identity = ((FactoryBean)bean.getInstance()).getBeanIdentity();
			if (beanCache.containsKey(identity)) {
				throw new BeanException(String.format("Bean[type:%s name:%s] 定义与BeanFactory[type:%s name:%s]冲突!", identity.getType().getName(), identity.getName(), bean.getType().getName(), bean.getName()));
			}
			BeanWrapper targetBeanWrapper = addBean(identity.getType(), identity.getName());
			targetBeanWrapper.setFactoryBean(bean);
			targetBeanWrapper.setInstantiationMode(BeanInstantiationMode.FACTORY);
		}
	}

	@Override
	public void init() {
		lifeCycleManager.init(() -> {
			if (null != parent) {
				parent.init();
			}
			if (CollectionUtil.notEmpty(beanCache)) {
				beanCache.values().stream().filter(b -> BeanStatus.INJECT == b.getStatus()).forEach(bean -> bean.init());
			}
			log.info("bean工厂[{}]初始化.", getName());
			scheduledExecutor.scheduleWithFixedDelay(this::run, 0, 1, TimeUnit.SECONDS);
		});
	}

	@Override
	public void destroy() {
		lifeCycleManager.destroy(() -> {
			if (null != parent) {
				parent.destroy();
			}
			if (CollectionUtil.notEmpty(beanCache)) {
				beanCache.values().stream().filter(b -> BeanStatus.DESTROY != b.getStatus()).forEach(bean -> bean.destroy());
			}
			log.info("bean工厂[{}]销毁.", getName());
		});
	}

	/**
	 * 获取或创建实例
	 * @param beanList
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	protected <T> List<T> getOrNew(List<BeanWrapper> beanList, Object... args) throws BeanException {
		if (CollectionUtil.isEmpty(beanList)) {
			return null;
		}
		dependencyCheck(beanList);
		newInstance(beanList, args);
		inject(beanList);
		beanList.forEach(beanWrapper -> beanWrapper.init());

		for (BeanWrapper beanWrapper : beanList) {
			if (beanWrapper.getStatus().getStatus() < BeanStatus.INIT.getStatus()) {
				throw new BeanException(String.format("Bean[type:%s name:%s]实例化失败!", beanWrapper.getType().getName(), beanWrapper.getName()));
			}
		}

		return beanList.stream().filter(beanWrapper -> BeanStatus.DESTROY != beanWrapper.getStatus()).map(beanWrapper -> (T)beanWrapper.getInstance()).collect(Collectors.toList());
	}

	/**
	 * 获取名称
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取生命周期状态
	 * @return
	 */
	public LifeCycleStatus getLifeCycleStatus() {
		return lifeCycleManager.getStatus();
	}

	/**
	 * 获取生命周期管理对象
	 * @return
	 */
	public LifeCycleManager getLifeCycleManager() {
		return lifeCycleManager;
	}

	/**
	 * 新增一个bean
	 * @param type
	 * @param name
	 */
	protected abstract BeanWrapper addBean(Class<?> type, String name);

	/**
	 * 获取或创建实例
	 * @param bean
	 * @param <T>
	 * @return
	 */
	protected <T> T getOrNew(BeanWrapper bean, Object... args) throws BeanException {
		try {
			return (T)LockUtil.doubleCheckProcess(
				() -> !(BeanStatus.INIT == bean.getStatus() || BeanStatus.RUNNING == bean.getStatus()),
				bean,
				() -> {
					if (BeanStatus.REGISTER == bean.getStatus()) {
						dependencyCheck(bean);
					}
					if (BeanStatus.DEPENDENCY_CHECKING == bean.getStatus()) {
						newInstance(bean);
					}
					if (BeanStatus.INSTANCE == bean.getStatus()) {
						inject(bean);
					}
					if (BeanStatus.INJECT == bean.getStatus()) {
						bean.init();
					}
				},
				() -> bean.getInstance()
			);
		} catch (Exception e) {
			throw new BeanException(String.format("Bean[type:%s name:%s] getOrNew bean实例异常!", bean.getType().getName(), bean.getName()), e);
		}
	}

	/**
	 * 依赖关系检测
	 * @param beanList
	 * @return
	 * @throws BeanException
	 */
	protected void dependencyCheck(List<BeanWrapper> beanList) throws BeanException {
		for (BeanWrapper beanWrapper : beanList) {
			dependencyCheck(beanWrapper);
		}
	}

	/**
	 * 实例化
	 * @param beanList
	 * @return
	 * @throws BeanException
	 */
	protected void newInstance(List<BeanWrapper> beanList, Object... args) throws BeanException {
		for (BeanWrapper beanWrapper : beanList) {
			newInstance(beanWrapper, args);
		}
	}

	/**
	 * 注入
	 * @param beanList
	 * @return
	 * @throws BeanException
	 */
	protected void inject(List<BeanWrapper> beanList) throws BeanException {
		for (BeanWrapper beanWrapper : beanList) {
			inject(beanWrapper);
		}
	}

	/**
	 * 依赖关系检测
	 * @param bean
	 * @return
	 * @throws BeanException
	 */
	protected abstract void dependencyCheck(BeanWrapper bean) throws BeanException;

	/**
	 * 实例化
	 * @param bean
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	protected abstract <T> T newInstance(BeanWrapper bean, Object... args) throws BeanException;

	/**
	 * 注入
	 * @param bean
	 * @return
	 * @throws BeanException
	 */
	protected abstract void inject(BeanWrapper bean) throws BeanException;

	/**
	 * 运行
	 */
	private void run() {
		beanCache.values().stream().filter(b -> b.getStatus().getStatus() < BeanStatus.RUNNING.getStatus()).forEach(b -> {
			try {
				getOrNew(b);
				b.run(getEnvironment().getMainArgs());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 获取环境参数
	 * @return
	 */
	private Environment getEnvironment() {
		return LockUtil.doubleCheckProcessForNoException(
			() -> null == environment,
			this,
			() -> environment = getBean(Environment.class),
			() -> environment
		);
	}
}
