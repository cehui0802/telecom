package com.telecom.ecloudframework.sys.util;

import java.io.OutputStream;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExeclUtil {
    static int sheetsize = 5000;

    /**
     * @param data   导入到excel中的数据
     * @param out    数据写入的文件
     * @param fields 需要注意的是这个方法中的map中：每一列对应的实体类的英文名为键，excel表格中每一列名为值
     * @throws Exception
     * @author Lyy
     */
    public static void ListtoExecl(List<Map> data, OutputStream out,
                                   LinkedHashMap<String, String> fields) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 如果导入数据为空，则抛出异常。
        if (data == null || data.size() == 0) {
            throw new Exception("导入的数据为空");
        }
        // 根据data计算有多少页sheet
        int pages = data.size() / sheetsize;
        if (data.size() % sheetsize > 0) {
            pages += 1;
        }
        // 提取表格的字段名（英文字段名是为了对照中文字段名的）
        String[] egtitles = new String[fields.size()];
        String[] cntitles = new String[fields.size()];
        Iterator<String> it = fields.keySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            String egtitle = it.next();
            String cntitle = fields.get(egtitle);
            egtitles[count] = egtitle;
            cntitles[count] = cntitle;
            count++;
        }
        // 添加数据
        for (int i = 0; i < pages; i++) {
            int rownum = 0;
            // 计算每页的起始数据和结束数据
            int startIndex = i * sheetsize;
            int endIndex = (i + 1) * sheetsize - 1 > data.size() ? data.size()
                    : (i + 1) * sheetsize - 1;
            // 创建每页，并创建第一行
            HSSFSheet sheet = workbook.createSheet();
            HSSFRow row = sheet.createRow(rownum);

            // 在每页sheet的第一行中，添加字段名
            for (int f = 0; f < cntitles.length; f++) {
                HSSFCell cell = row.createCell(f);
                cell.setCellValue(cntitles[f]);
            }
            rownum++;
            // 将数据添加进表格
            for (int j = startIndex; j < endIndex; j++) {
                row = sheet.createRow(rownum);
                Map<String,Object> item = data.get(j);
                for (int h = 0; h < cntitles.length; h++) {
                    Object o = item.get(egtitles[h]);
                    String value = o == null ? "" : o.toString();
                    HSSFCell cell = row.createCell(h);
                    cell.setCellValue(value);
                }
                rownum++;
            }
        }
        // 将创建好的数据写入输出流
        workbook.write(out);
        workbook.close();
    }
}