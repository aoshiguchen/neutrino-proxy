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
package fun.asgc.neutrino.core.bean.test4;

import fun.asgc.neutrino.core.annotation.NeutrinoApplication;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.aop.support.Async;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.context.NeutrinoLauncher;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/8
 */
@NonIntercept(false)
@NeutrinoApplication
public class Launcher implements ApplicationRunner {


	@Override
	public void run(String[] args) {
		System.out.println("1111111");
		hello();
		System.out.println("2222");
	}

	public static void main(String[] args) {
		NeutrinoLauncher.run(Launcher.class, args).sync();
	}

	@Async
	public void hello() {
		try {
			Thread.sleep(5000);
			System.out.println("hello");
		} catch (Exception e) {

		}
	}

}
