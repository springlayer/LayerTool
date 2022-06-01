package org.springlayer.core.flowable.controller;

import org.flowable.ui.modeler.rest.app.ModelBpmnResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description 下载xml文件 controller
 **/
@RestController
@RequestMapping("/modeler/app")
public class ModelBpmnResourceController {

    @Resource
    private ModelBpmnResource modelBpmnResource;

    /**
     * GET /rest/models/{modelId}/bpmn20 -> Get BPMN 2.0 xml
     */
    @RequestMapping(value = "/rest/models/{processModelId}/bpmn20", method = RequestMethod.GET)
    public void getProcessModelBpmn20Xml(HttpServletResponse response, @PathVariable String processModelId) throws IOException {
        modelBpmnResource.getProcessModelBpmn20Xml(response,processModelId);
    }

}
