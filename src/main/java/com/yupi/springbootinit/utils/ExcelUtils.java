package com.yupi.springbootinit.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
public class ExcelUtils {

    public static String excelToCsv(MultipartFile multipartFile)  {

        //校验文件是否合规

        long size = multipartFile.getSize();
        //最大不能超过一M
        final long maxSize=1024*1024L;
        ThrowUtils.throwIf(size>maxSize, ErrorCode.SYSTEM_ERROR,"文件太大");
        //判断后缀名
        String multipartFileName = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(multipartFileName);
        final List<String> suffixName= Arrays.asList("xlsx","xls");
        ThrowUtils.throwIf(!suffixName.contains(suffix),ErrorCode.SYSTEM_ERROR,"文件后缀名错误");

        List<Map<Integer, String>> list = null;
        try {
                list = EasyExcel.read( multipartFile.getInputStream())
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("数据解析错误",e);
        }
        if (CollectionUtils.isEmpty(list)){
            return "";
        }
        StrBuilder strBuilder = new StrBuilder();
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        strBuilder.append(StringUtils.join(headerList,",")).append("\n");
        // 读取数据(读取完表头之后，从第一行开始读取)
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            strBuilder.append(StringUtils.join(dataList,",")).append("\n");
        }
        return strBuilder.toString();
    }


}
