package org.springlayer.core.flowable.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description
 **/
@RestController
@RequestMapping("/modeler/app")
public class StencilSetResourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StencilSetResourceController.class);

    private static final String CN = "zh-CN";

    @Resource
    protected ObjectMapper objectMapper;

    @RequestMapping(value = "/rest/stencil-sets/editor", method = RequestMethod.GET, produces = "application/json")
    public JsonNode getStencilSetForEditor(HttpServletRequest request) {
        String stencilsetFileName = "";
        LOGGER.debug("当前环境语言：{}", request.getLocale().toLanguageTag());

        if (CN.equals(request.getLocale().toLanguageTag())) {
            stencilsetFileName = "stencilset_bpmn_" + request.getLocale().toLanguageTag() + ".json";
        } else {
            stencilsetFileName = "stencilset_bpmn_en.json";
        }

        LOGGER.debug("加载的资源文件名应为：{}", stencilsetFileName);
        try {
            JsonNode stencilNode = objectMapper.readTree(
                    this.getClass().getClassLoader().getResourceAsStream(stencilsetFileName));
            return stencilNode;
        } catch (Exception e) {
            LOGGER.error("Error reading bpmn stencil set json", e);
            throw new InternalServerErrorException("Error reading bpmn stencil set json");
        }
    }
}
