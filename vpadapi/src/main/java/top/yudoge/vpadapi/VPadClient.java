package top.yudoge.vpadapi;

/**
 * VPadClient是一个客户端对象，它的功能只有一个，就是连接到一个VPadServer，并返回一个VPadConnection
 */
public interface VPadClient {
    /**
     *
     * @param vPadServer
     * @throws ClientException 当连接出现任何异常，都会抛出ClientException，msg即异常原因
     * @return 若成功连接，返回的是代表此次连接的VPadConnection对象，若未成功连接，一定是某个步骤中出现了异常，抛出ClientException
     */
    VPadConnection connect(VPadServer vPadServer);
}
