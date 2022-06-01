package org.springlayer.core.flowable.controller;

import org.flowable.ui.modeler.model.AppDefinitionPublishRepresentation;
import org.flowable.ui.modeler.model.AppDefinitionRepresentation;
import org.flowable.ui.modeler.model.AppDefinitionSaveRepresentation;
import org.flowable.ui.modeler.model.AppDefinitionUpdateResultRepresentation;
import org.flowable.ui.modeler.rest.app.AppDefinitionResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description app操作 controller
 **/
@RestController
@RequestMapping("/modeler/app")
public class AppDefinitionResourceController {

    @Resource
    private AppDefinitionResource appDefinitionResource;

    @RequestMapping(value = "/rest/app-definitions/{modelId}", method = RequestMethod.GET, produces = "application/json")
    public AppDefinitionRepresentation getAppDefinition(@PathVariable("modelId") String modelId) {
        return appDefinitionResource.getAppDefinition(modelId);
    }

    @RequestMapping(value = "/rest/app-definitions/{modelId}", method = RequestMethod.PUT, produces = "application/json")
    public AppDefinitionUpdateResultRepresentation updateAppDefinition(@PathVariable("modelId") String modelId, @RequestBody AppDefinitionSaveRepresentation updatedModel) {
        return appDefinitionResource.updateAppDefinition(modelId, updatedModel);
    }

    @RequestMapping(value = "/rest/app-definitions/{modelId}/publish", method = RequestMethod.POST, produces = "application/json")
    public AppDefinitionUpdateResultRepresentation publishAppDefinition(@PathVariable("modelId") String modelId, @RequestBody AppDefinitionPublishRepresentation publishModel) {
        return appDefinitionResource.publishAppDefinition(modelId, publishModel);
    }

}
