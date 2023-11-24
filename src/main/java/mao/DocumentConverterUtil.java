package mao;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.Properties;

/**
 * Project name(项目名称)：openoffice-word-to-pdf2
 * Package(包名): mao
 * Class(类名): DocumentConverterUtil
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/11/24
 * Time(创建时间)： 23:53
 * Version(版本): 1.0
 * Description(描述)： 文档转换工具
 */

public class DocumentConverterUtil
{
    private static SocketOpenOfficeConnection connection = null;

    private static DocumentConverter documentConverter = null;

    private static Properties properties = null;

    private static int serviceCheckCycle;

    static
    {
        InputStream inputStream = DocumentConverterUtil.class.getClassLoader().getResourceAsStream("conf/openoffice.properties");
        try
        {
            properties = new Properties();
            properties.load(inputStream);
            String host = properties.getProperty("server.host");
            Integer port = Integer.parseInt(properties.getProperty("server.port"));
            serviceCheckCycle = Integer.parseInt(properties.getProperty("serviceCheckCycle"));
            connection = new SocketOpenOfficeConnection(host, port);
            connection.connect();
            documentConverter = new StreamOpenOfficeDocumentConverter(connection);
        }
        catch (ConnectException e)
        {
            e.printStackTrace();
            System.out.println("文档转换服务连接失败");
            //throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("加载配置文件失败或者其它问题");
            //throw new RuntimeException(e);
        }
        finally
        {
            //服务检查
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            Thread.sleep(serviceCheckCycle);
                            if (isNotConnection())
                            {
                                System.out.println("openoffice服务异常，正在尝试重连");
                                retry();
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                if (connection.isConnected())
                {
                    System.out.println("正在关闭openoffice连接...");
                    connection.disconnect();
                }
            }));
        }
    }

    /**
     * 服务重连
     *
     * @return 重连结果
     */
    public static boolean retry()
    {
        System.out.println("尝试重连openoffice");
        String host = properties.getProperty("server.host");
        Integer port = Integer.parseInt(properties.getProperty("server.port"));
        connection = new SocketOpenOfficeConnection(host, port);
        documentConverter = new StreamOpenOfficeDocumentConverter(connection);
        try
        {
            connection.connect();
            if (isConnection())
            {
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            System.out.println("重连失败");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 得到当前properties
     *
     * @return properties对象
     */
    public static Properties getProperties()
    {
        return properties;
    }

    /**
     * 判断服务是否正常连接
     *
     * @return 成功连接为true
     */
    public static boolean isConnection()
    {
        if (connection == null || !connection.isConnected())
        {
            return false;
        }
        return true;
    }

    /**
     * 判断服务是否没有连接
     *
     * @return 成功连接为false
     */
    public static boolean isNotConnection()
    {
        return !isConnection();
    }


    /**
     * 文档转换
     *
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     */
    public static void converter(String sourcePath, String targetPath)
    {
        documentConverter.convert(new File(sourcePath), new File(targetPath));
    }

    /**
     * 文档转换
     *
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     */
    public static void converter(File sourcePath, File targetPath)
    {
        documentConverter.convert(sourcePath, targetPath);
    }

    /**
     * 文档转换
     *
     * @param inputStream      输入流
     * @param outputStream     输出流
     * @param sourceFileSuffix 源文件后缀
     * @param targetFileSuffix 目标文件后缀
     */
    public static void converter(InputStream inputStream, OutputStream outputStream,
                                 String sourceFileSuffix, String targetFileSuffix)
    {
        DocumentFormat sourceDocumentFormat = new DefaultDocumentFormatRegistry()
                .getFormatByFileExtension(sourceFileSuffix);
        DocumentFormat targetDocumentFormat = new DefaultDocumentFormatRegistry()
                .getFormatByFileExtension(targetFileSuffix);
        documentConverter.convert(inputStream, sourceDocumentFormat, outputStream, targetDocumentFormat);
    }
}
