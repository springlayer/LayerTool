package org.springlayer.core.flowable.controller;


import com.fasterxml.jackson.databind.JsonNode;
import org.flowable.ui.modeler.rest.app.EditorDisplayJsonClientResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description 单个modelers查看操作 controller
 **/
@RestController
@RequestMapping("/modeler/app")
public class EditorDisplayJsonClientResourceController {

    @Resource
    EditorDisplayJsonClientResource editorDisplayJsonClientResource;

    @RequestMapping(value = "/rest/models/{modelId}/model-json", method = RequestMethod.GET, produces = "application/json")
    public JsonNode getModelJSON(@PathVariable String modelId) {
        return editorDisplayJsonClientResource.getModelJSON(modelId);
    }

}
