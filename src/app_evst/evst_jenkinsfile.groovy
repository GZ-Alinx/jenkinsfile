#!/groovy
// jenkins共享库引入，在jenkns中配置
//@Library('github@master')
@Library('jenkinslib@master')

import hudson.model.*


// 共享库方法引用，结合jenkins共享库引入
def mail =  new org.devops.mail()


pipeline {
    // 构建节点
    agent {
        node {
            label "master"
            customWorkspace "workspace/$JOB_NAME/$BUILD_NUMBER" // job路径以每一次的构建编号为准
        }
    }

    // 流水线配置
    options {
        // 禁止同时执行
        disableConcurrentBuilds()
        // 流水线超时时间
        timeout(time: 120,unit: 'MINUTES')
    }

    // 流水线参数
    parameters {
        // 字符串参数
        string(name: "str", defaultValue: "默认参数", description: "说明")

        // 文本参数
        text(name: "text", defaultValue: "默认参数", description: "说明")

        // 布尔类型参数
        booleanParam(name: "bools", defaultValue: true, description: "说明")

        // 选择参数
        choice(name: "action",description: "说明",choices:['a1','b2','c3'])

        // 文件参数
        file(name: "file", description: "说明")

        // 密码参数
        password(name: 'password', defaultValue: "123", description: "说明")
    }

    stages {
        stage("配置读取"){
            steps{
                script {
                    json = load "$WORKSPACE/src/app_evst/script/read_json.groovy"
                    json_file = "$WORKSPACE/src/app_evst/script/config.json"
                    def config = json.ToFile_read_json(json_file)
                    println(config["host"]["hostip"])
                    println(config["host"]["port"])
                    println(config["host"]["password"])
                }
            }
        }
        stage('步骤1') {
            steps {
                script {
                    println "你的字符串参数为: ${str}"
                    println "你的文本参数为: ${text}"
                    println "你的布尔参数为: ${bools}"
                    println "你的选择的参数为: ${action}"
                    println "你的文件参数为: ${file}"
                    println "你的密码参数为: ${password}"

                    input "步骤卡点"
                }
            }
        }
        stage('步骤2') {
            steps {
                script {
                    println "具体实现方法"
                    // env.action = input message: "提示",ok:'确认',parameters:[具体参数类型];
                    // 相当于将后面的值赋给签名的变量
                    env.action = input message: "提示",ok:'确认',parameters:[choice(description: "说明", name: "请选择", choices:['yes','no'],trim:false)]
                }
            }
        }
        stage('步骤3'){
            steps {
                script {
                    if(env.action == 'yes') {
                        println "yes"
                    }else {
                        println "no"
                    }
                }
            }
        }
    }

    // 根据流水线操作进行操作
    post {
        always {
            script{
                println "总是会执行"
            }
        }
        success {
            script{
                println "成功后会执行"
            }
        }
        failure {
            script{
                println "失败后执行"
            }
        }
        aborted {
            script{
                println "取消后会执行"
            }
        }
    }
}
