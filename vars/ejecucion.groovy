def call(){
    pipeline {
        agent any
        environment {
            NEXUS_USER         = credentials('token-nexus-curl-useradmin')
            NEXUS_PASSWORD     = credentials('token-nexus-curl-passadmin')
        }
        parameters {
            choice(
                name:'compileTool',
                choices: ['Maven', 'Gradle'],
                description: 'Seleccione herramienta de compilacion'
            )
            string(
                name:'stages',
                description: 'Ingrese los stages para ejecutar',
                trim: true
            )
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                        sh "echo 'Rama detectada: $BRANCH_NAME!'"
                        if(env.BRANCH_NAME.toString().contains('develop')){
                                sh "echo 'Ejecutando Pipeline Develop'"
                                ci.call(params.stages)
                        } else if(env.BRANCH_NAME.toString().contains('feature')){
                                sh "echo 'Ejecutando Pipeline Feature para $BRANCH_NAME'"
                                ci.call(params.stages)
                        } else if(env.BRANCH_NAME.toString().contains('release')){
                                sh "echo 'Ejecutando Pipeline Release para $BRANCH_NAME'"
                                cd.call(params.stages)
                        } else {
                            sh "echo 'La rama $BRANCH_NAME no tiene asociado un pipeline disponible'"
                        }
                        env.STAGE  = env.STAGE_NAME
                        print 'Compile Tool: ' + params.compileTool;
                        /*switch(params.compileTool){
                                case 'Maven':
                                    figlet  "Maven"
                                    maven.call(params.stages)
                                break;
                                case 'Gradle':
                                    figlet  "Gradle"
                                    gradle.call(params.stages)
                                break;
                        }*/
                    }
                }
            }
        }
        post {
            success{
                slackSend color: 'good', message: "[Grupo 3][${JOB_NAME}][${params.compileTool}] Ejecución Exitosa.", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
            }
            failure{
                slackSend color: 'danger', message: "[Grupo 3][${JOB_NAME}][${params.compileTool}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
            }
        }
    }
}

return this;