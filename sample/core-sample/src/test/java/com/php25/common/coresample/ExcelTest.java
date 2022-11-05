package com.php25.common.coresample;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.coresample.dto.CustomerDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger log = LoggerFactory.getLogger(ExcelTest.class);

    @Test
    public void test() throws Exception {
        objToExcel();
        excelToObj();
    }


    public void objToExcel() throws Exception {
        Path path = Paths.get("/tmp/1.xls");
        if (Files.exists(path)) {
            Files.delete(path);
        }
        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername("test");
        customerDto.setCreateTime(new Date());
        customerDto.setUpdateTime(new Date());
        customerDto.setEnable(1);
        customerDto.setId(1L);
        customerDto.setPassword("1231312312333");
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CustomerDto.class, Lists.newArrayList(customerDto));
        workbook.write(Files.newOutputStream(path));
        Assertions.assertThat(Files.exists(path));
    }

    public void excelToObj() throws Exception {
        objToExcel();
        Path path = Paths.get("/tmp/1.xls");
        List<CustomerDto> customerDtoList = ExcelImportUtil.importExcel(Files.newInputStream(path), CustomerDto.class, new ImportParams());
        log.info("客户列表为:{}", JsonUtil.toJson(customerDtoList));
        Assertions.assertThat(customerDtoList.size()).isEqualTo(1);
        Assertions.assertThat(customerDtoList.get(0).getUsername()).isEqualTo("test");
        Assertions.assertThat(customerDtoList.get(0).getPassword()).isEqualTo("1231312312333");
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

}
