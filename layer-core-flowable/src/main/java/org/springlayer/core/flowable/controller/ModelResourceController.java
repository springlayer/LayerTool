package org.springlayer.core.flowable.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.rest.app.ModelResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description 单个modelers操作 controller
 **/
@RestController
@RequestMapping("/modeler/app")
public class ModelResourceController {

    @Resource
    private ModelResource modelResource;

    /**
     * GET /rest/models/{modelId} -> Get process model
     */
    @RequestMapping(value = "/rest/models/{modelId}", method = RequestMethod.GET, produces = "application/json")
    public ModelRepresentation getModel(@PathVariable String modelId) {
        return modelResource.getModel(modelId);
    }

    /**
     * GET /rest/models/{modelId}/editor/json -> get the JSON model
     */
    @RequestMapping(value = "/rest/models/{modelId}/editor/json", method = RequestMethod.GET, produces = "application/json")
    public ObjectNode getModelJson(@PathVariable String modelId) {
        return modelResource.getModelJSON(modelId);
    }

    /**
     * POST /rest/models/{modelId}/editor/json -> save the JSON model
     */
    @RequestMapping(value = "/rest/models/{modelId}/editor/json", method = RequestMethod.POST)
    public ModelRepresentation saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
        //始终发布新版本
        //values.put("newversion", Lists.newArrayList("true"));
        //不允许修改key,key被原始的覆盖。
        ModelRepresentation modelRepresentation = modelResource.getModel(modelId);
        //values.set("key", modelRepresentation.getKey());
        modelRepresentation = modelResource.saveModel(modelId, values);
        return modelRepresentation;
    }

    /**
     * GET /rest/models/{modelId}/thumbnail -> Get process model thumbnail
     */
    @RequestMapping(value = "/rest/models/{modelId}/thumbnail", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getModelThumbnail(@PathVariable String modelId) {
        return modelResource.getModelThumbnail(modelId);
    }

    /**
     * DELETE /rest/models/{modelId} -> delete process model or, as a non-owner, remove the share info link for that user specifically
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/rest/models/{modelId}", method = RequestMethod.DELETE)
    public void deleteModel(@PathVariable String modelId) {
        modelResource.deleteModel(modelId);
    }

    /**
     * PUT /rest/models/{modelId} -> update process model properties
     */
    @RequestMapping(value = "/rest/models/{modelId}", method = RequestMethod.PUT)
    public ModelRepresentation updateModel(@PathVariable String modelId, @RequestBody ModelRepresentation updatedModel) {
        return modelResource.updateModel(modelId, updatedModel);
    }

}
