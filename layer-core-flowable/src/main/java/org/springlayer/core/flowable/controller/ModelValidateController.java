package org.springlayer.core.flowable.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springlayer.core.flowable.convert.IrisFlowableValidator;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ValidatorSet;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * @author zhaoyl
 * @Date 2022-05-10
 * 自定义验证
 */
@RestController
@RequestMapping("/modeler/app")
public class ModelValidateController {

    @RequestMapping(value = "/rest/model/validate",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ValidationError> validate(@RequestBody JsonNode body){
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(body);
        ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
        ValidatorSet validatorSet = new ValidatorSet("验证用户任务分配");
        validatorSet.addValidator(new IrisFlowableValidator());
        validator.getValidatorSets().add(validatorSet);
        List<ValidationError> errors = validator.validate(bpmnModel);
        return errors;
    }
}
