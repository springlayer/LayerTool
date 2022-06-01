package org.springlayer.core.flowable.controller;

import org.flowable.ui.modeler.domain.ModelInformation;
import org.flowable.ui.modeler.rest.app.ModelRelationResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description
 **/
@RestController
@RequestMapping("/modeler/app")
public class ModelRelationResourceController {

    @Resource
    private ModelRelationResource modelRelationResource;

    @RequestMapping(value = "/rest/models/{modelId}/parent-relations", method = RequestMethod.GET, produces = "application/json")
    public List<ModelInformation> getModelRelations(@PathVariable String modelId) {
        return modelRelationResource.getModelRelations(modelId);
    }

}
