def call(){
    pipeline {
        agent any
        trigger {
            GenericTrigger{
                genericVariables: [
                    [key: 'ref', value: '$.ref']
                ],
                genericRequestVariables: [
                    [key: 'compileTool', regexpFilter: ''],
                    [key: 'stages', regexpFilter: '']
                ],
                causeString: 'Triggered on $compileTool',
                token: 'abc123',
                tokenCredentialId: '',
                printContributedVariables: true,
                silentResponse: true,
                regextpFilterText: '$ref',
                regexpFilterExpression: 'refs/heads/' + BRANCH_NAME
            }
        }
        environment {
            NEXUS_USER         = credentials('token-nexus-curl-useradmin')
            NEXUS_PASSWORD     = credentials('token-nexus-curl-passadmin')
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                        sh "echo 'Rama detectada: $BRANCH_NAME!'"
                        env.STAGE  = env.STAGE_NAME
                        env.COMPILE_TOOL = compileTool
                        print 'Compile Tool: ' + compileTool;
                        switch(env.COMPILE_TOOL){
                            case 'Maven':
                                figlet  "Maven"
                            break;
                            case 'Gradle':
                                figlet  "Gradle"
                            break;
                        }
                        if(env.BRANCH_NAME.toString().contains("develop")){
                                sh "echo 'Ejecutando Pipeline Develop'"
                                ci.call(stages,compileTool)
                        } else if(env.BRANCH_NAME.toString().contains("feature")){
                                sh "echo 'Ejecutando Pipeline Feature para $BRANCH_NAME'"
                                ci.call(stages,compileTool)
                        } else if(env.BRANCH_NAME.toString().contains("release")){
                                sh "echo 'Ejecutando Pipeline Release para $BRANCH_NAME'"
                                cd.call(stages,compileTool)
                        } else {
                            sh "echo 'La rama $BRANCH_NAME no tiene asociado un pipeline disponible'"
                        }
                    }
                }
            }
        }
        post {
            success{
                slackSend color: 'good', message: "[Grupo 3][${JOB_NAME}][${compileTool}] Ejecución Exitosa.", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
            }
            failure{
                slackSend color: 'danger', message: "[Grupo 3][${JOB_NAME}][${compileTool}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
            }
        }
    }
}

return this;