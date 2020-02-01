package utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

public class JedisUtils {

    private static JedisPool pool;

    private static String host;
    private static int port;
    private static int maxTotal;
    private static int maxIdle;

    static{
        //加载资源文件
        ResourceBundle bundle = ResourceBundle.getBundle("jedis");
        host = bundle.getString("host");
        port = Integer.parseInt(bundle.getString("port"));
        maxTotal = Integer.parseInt(bundle.getString("maxTotal"));
        maxIdle = Integer.parseInt(bundle.getString("maxIdle"));

        //创建连接池配置信息
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);

        //创建连接池对象
        pool = new JedisPool(config, host, port);
    }

    /**
     * 获取Jedis连接
     * @return Jedis对象
     */
    public static Jedis getJedis(){
        return pool.getResource();
    }

    /**
     * 关闭Jedis连接
     * @param jedis 要关闭的Jedis连接对象
     */
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 设置缓存
     * @param key 缓存的key
     * @param value 缓存的value
     */
    public static void setCache(String key, String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key, value);
        } catch (Exception e) {
            System.out.println("设置缓存数据失败：["+key+":"+value+"]");
        } finally {
            close(jedis);
        }
    }

    /**
     * 获取缓存
     * @param key 缓存的key
     * @return 缓存的value
     */
    public static String getCache(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } catch (Exception e) {
            System.out.println("获取缓存失败：" + key);
        } finally {
            close(jedis);
        }
        return null;
    }
}
