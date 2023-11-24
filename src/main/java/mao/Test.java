package mao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Project name(项目名称)：openoffice-word-to-pdf2
 * Package(包名): mao
 * Class(类名): Test
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/11/25
 * Time(创建时间)： 0:05
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class Test
{
    public static void main(String[] args) throws FileNotFoundException
    {
        DocumentConverterUtil.converter("./test.docx", "./test.pdf");
        DocumentConverterUtil.converter("./test.docx", "./test2.pdf");
        DocumentConverterUtil.converter("./test.docx", "./test3.pdf");
        DocumentConverterUtil.converter(new FileInputStream("./test.docx"),
                new FileOutputStream("./test4.pdf"),"docx","pdf");
        System.out.println("转换完成");
    }
}
