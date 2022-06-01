package org.springlayer.core.flowable.controller;

import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.modeler.rest.app.FormsResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description form操作 controller
 **/
@RestController
@RequestMapping("/modeler/app/rest/form-models")
public class FormsResourceController {

    @Resource
    private FormsResource formsResource;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResultListDataRepresentation getForms(HttpServletRequest request) {
        return formsResource.getForms(request);
    }


}
