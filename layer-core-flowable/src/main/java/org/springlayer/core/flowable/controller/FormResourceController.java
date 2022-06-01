package org.springlayer.core.flowable.controller;


import org.flowable.ui.modeler.model.FormSaveRepresentation;
import org.flowable.ui.modeler.model.form.FormRepresentation;
import org.flowable.ui.modeler.rest.app.FormResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description 单个form操作 controller
 **/
@RestController
@RequestMapping("/modeler/app/rest/form-models")
public class FormResourceController {

    @Resource
    private FormResource formResource;

    @RequestMapping(value = "/{formId}", method = RequestMethod.GET, produces = "application/json")
    public FormRepresentation getForm(@PathVariable String formId) {
        return formResource.getForm(formId);
    }

    @RequestMapping(value = "/{formId}", method = RequestMethod.PUT, produces = "application/json")
    public FormRepresentation saveForm(@PathVariable String formId, @RequestBody FormSaveRepresentation saveRepresentation) {
        return formResource.saveForm(formId, saveRepresentation);
    }

}
