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
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                    print 'Compile Tool: ' + params.compileTool;
                    switch(params.compileTool)
                        {
                            case 'Maven':
                                print 'Ejecutando Maven'
                                maven.call()
                            break;
                            case 'Gradle':
                                print 'Ejecutando Gradle'
                                gradle.call()
                            break;
                        }
                    }
                }
            }
        }
        post {
            always {
                sh "echo 'fase always executed post'"
            }
            success {
                sh "echo 'fase success'"
            }
            failure {
                sh "echo 'fase failure in $env.STAGE'"
            }
        }
    }
}

return this;