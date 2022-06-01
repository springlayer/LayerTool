package org.springlayer.core.flowable.controller;


import org.springlayer.core.flowable.service.ModelDeploymentServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaoyl
 * @Date 2022-05-10
 * 部署模型
 */
@RestController
@RequestMapping("/modeler/sys")
public class ModelDeploymentController {
    @Resource
    private ModelDeploymentServiceImpl modelDeploymentService;

    //使用id部署
    @RequestMapping(value = "/rest/models/{modelId}", method = RequestMethod.GET)
    public String updateModel(@PathVariable String modelId) {
        return modelDeploymentService.deploymentProcessDefinitionFromUIModelId(modelId);
    }

    @RequestMapping(value = "/rest/models/file", method = RequestMethod.POST)
    public void saveModel(HttpServletRequest request,String processName) {
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        MultipartFile fileXml = multipartRequest.getFile("fileXml");
        MultipartFile fileImg = multipartRequest.getFile("fileImg");
        System.out.println(1111);
    }

}
