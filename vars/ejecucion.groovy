/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

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
                    env.STAGE  = env.STAGE_NAME
                    print 'Compile Tool: ' + params.compileTool;
                    switch(params.compileTool)
                        {
                            case 'Maven':
                                figlet  "Maven"
                                maven.call(params.stages)
                            break;
                            case 'Gradle':
                                figlet  "Gradle"
                                gradle.call(params.stages)
                            break;
                        }
                    }
                }
            }
        }
        post {
            success{
                slackSend color: 'good', message: "[Felipe Contreras][${JOB_NAME}][${params.compileTool}] Ejecución Exitosa.", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
            }
            failure{
                slackSend color: 'danger', message: "[Felipe Contreras][${JOB_NAME}][${params.compileTool}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
            }
        }
    }
}

return this;