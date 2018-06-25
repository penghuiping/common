package com.php25.common;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.php25.common.dto.CustomerDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/25 10:41
 * @Description:
 */
public class ExcelTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ExcelTest.class);

    @Test
    public void objToExcel() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername("test");
        customerDto.setCreateTime(new Date());
        customerDto.setUpdateTime(new Date());
        customerDto.setEnable(1);
        customerDto.setId(1l);
        customerDto.setPassword("1231312312333");

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CustomerDto.class, Lists.newArrayList(customerDto));

        Path path = Paths.get("/Users/penghuiping/Desktop/joinsoft-docker/1.xls");
        try {
            workbook.write(Files.newOutputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void excelToObj() {
        Path path = Paths.get("/Users/penghuiping/Desktop/joinsoft-docker/1.xls");
        try {
            List<CustomerDto> customerDtoList = ExcelImportUtil.importExcel(Files.newInputStream(path), CustomerDto.class, new ImportParams());
            logger.info(objectMapper.writeValueAsString(customerDtoList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
