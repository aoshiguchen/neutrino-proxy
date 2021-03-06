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

package fun.asgc.neutrino.proxy.server.util;

import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.server.config.ProxyServerConfig;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class ProxyChannelManager {

    private static final AttributeKey<Map<String, Channel>> USER_CHANNELS = AttributeKey.newInstance("user_channels");

    private static final AttributeKey<String> REQUEST_LAN_INFO = AttributeKey.newInstance("request_lan_info");

    private static final AttributeKey<List<Integer>> CHANNEL_PORT = AttributeKey.newInstance("channel_port");

    private static final AttributeKey<String> CHANNEL_CLIENT_KEY = AttributeKey.newInstance("channel_client_key");

    private static Map<Integer, Channel> portCmdChannelMapping = new ConcurrentHashMap<Integer, Channel>();

    private static Map<String, Channel> cmdChannels = new ConcurrentHashMap<String, Channel>();

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param ports
     * @param channel
     */
    public static void addCmdChannel(List<Integer> ports, String clientKey, Channel channel) {
        if (ports == null) {
            throw new IllegalArgumentException("port can not be null");
        }

        // ????????????proxy-client??????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????????????????????????????removeChannel(Channel channel)???????????????
        synchronized (portCmdChannelMapping) {
            for (int port : ports) {
                portCmdChannelMapping.put(port, channel);
            }
        }

        channel.attr(CHANNEL_PORT).set(ports);
        channel.attr(CHANNEL_CLIENT_KEY).set(clientKey);
        channel.attr(USER_CHANNELS).set(new ConcurrentHashMap<String, Channel>());
        cmdChannels.put(clientKey, channel);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param channel
     */
    public static void removeCmdChannel(Channel channel) {
        if (channel.attr(CHANNEL_PORT).get() == null) {
            return;
        }

        String clientKey = channel.attr(CHANNEL_CLIENT_KEY).get();
        Channel channel0 = cmdChannels.remove(clientKey);
        if (channel != channel0) {
            cmdChannels.put(clientKey, channel);
        }

        List<Integer> ports = channel.attr(CHANNEL_PORT).get();
        for (int port : ports) {
            Channel proxyChannel = portCmdChannelMapping.remove(port);
            if (proxyChannel == null) {
                continue;
            }

            // ???????????????????????????????????????????????????
            if (proxyChannel != channel) {
                portCmdChannelMapping.put(port, proxyChannel);
            }
        }

        if (channel.isActive()) {
            channel.close();
        }

        Map<String, Channel> userChannels = getUserChannels(channel);
        Iterator<String> ite = userChannels.keySet().iterator();
        while (ite.hasNext()) {
            Channel userChannel = userChannels.get(ite.next());
            if (userChannel.isActive()) {
                userChannel.close();
            }
        }
    }

    public static Channel getCmdChannel(Integer port) {
        return portCmdChannelMapping.get(port);
    }

    public static Channel getCmdChannel(String clientKey) {
        return cmdChannels.get(clientKey);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param userId
     * @param userChannel
     */
    public static void addUserChannelToCmdChannel(Channel cmdChannel, String userId, Channel userChannel) {
        InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
        String lanInfo = ProxyServerConfig.getInstance().getLanInfo(sa.getPort());
        userChannel.attr(Constants.USER_ID).set(userId);
        userChannel.attr(REQUEST_LAN_INFO).set(lanInfo);
        cmdChannel.attr(USER_CHANNELS).get().put(userId, userChannel);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param userId
     * @return
     */
    public static Channel removeUserChannelFromCmdChannel(Channel cmdChannel, String userId) {
        if (cmdChannel.attr(USER_CHANNELS).get() == null) {
            return null;
        }

        synchronized (cmdChannel) {
            return cmdChannel.attr(USER_CHANNELS).get().remove(userId);
        }
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param userId
     * @return
     */
    public static Channel getUserChannel(Channel cmdChannel, String userId) {
        return cmdChannel.attr(USER_CHANNELS).get().get(userId);
    }

    /**
     * ??????????????????
     *
     * @param userChannel
     * @return
     */
    public static String getUserChannelUserId(Channel userChannel) {
        return userChannel.attr(Constants.USER_ID).get();
    }

    /**
     * ???????????????????????????IP????????????
     *
     * @param userChannel
     * @return
     */
    public static String getUserChannelRequestLanInfo(Channel userChannel) {
        return userChannel.attr(REQUEST_LAN_INFO).get();
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param cmdChannel
     * @return
     */
    public static Map<String, Channel> getUserChannels(Channel cmdChannel) {
        return cmdChannel.attr(USER_CHANNELS).get();
    }

}
