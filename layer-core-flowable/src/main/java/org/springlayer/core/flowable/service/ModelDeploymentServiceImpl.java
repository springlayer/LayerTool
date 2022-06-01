package org.springlayer.core.flowable.service;

import org.flowable.bpmn.model.BpmnModel;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipInputStream;

/**
 * @author zhaoyl
 * @Date 2022-05-10
 * 流程部署
 */
@Service
public class ModelDeploymentServiceImpl {
    @Autowired
    private ModelService modelService;
    @Autowired
    private RepositoryService repositoryService;


    /**部署流程定义(根据ui.modeler的 modelId部署)
     * @param modelId 模型ID
     * @from fhadmin.cn
     */
    public String deploymentProcessDefinitionFromUIModelId(String modelId){
        Model model = modelService.getModel(modelId);
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .addBpmnModel(model.getKey() + ".bpmn", bpmnModel).deploy();
        return deployment.getId(); //部署ID
    }

    /**部署流程定义(从Classpath)
     * @param name       //部署名称
     * @param xmlpath    //xml文件路径
     * @param pngpath    //png文件路径
     * @from fhadmin.cn
     */
    public String deploymentProcessDefinitionFromClasspath(String name, String xmlpath, String pngpath){
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();      //创建部署对象
        deploymentBuilder.name(name);                       //部署名称
        deploymentBuilder.addClasspathResource(xmlpath);    //从文件中读取xml资源
        deploymentBuilder.addClasspathResource(pngpath);    //从文件中读取png资源
        Deployment deployment = deploymentBuilder.deploy();  //完成部署
        return deployment.getId();                         //部署ID
    }

    /**部署流程定义(从zip压缩包)
     * @param name       //部署名称
     * @param zippath    //zip文件路径
     * @from fhadmin.cn
     * @throws
     */
    public String deploymentProcessDefinitionFromZip(String name, String zippath) throws Exception{
        File outfile = new File(zippath);
        FileInputStream inputStream = new FileInputStream(outfile);
        ZipInputStream ipInputStream = new ZipInputStream(inputStream);
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment(); //创建部署对象
        deploymentBuilder.name(name);                       //部署名称
        deploymentBuilder.addZipInputStream(ipInputStream);
        Deployment deployment = deploymentBuilder.deploy();  //完成部署
        ipInputStream.close();
        inputStream.close();
        return deployment.getId();                         //部署ID
    }
}
