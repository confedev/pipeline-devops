/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/
def call(){
    env.STAGE = 'Paso 1: Build and Test'
    stage("$env.STAGE"){
        sh "gradle clean build"
    }
    env.STAGE = 'Paso 2: Sonar - Análisis Estático'
    stage("$env.STAGE"){
        sh "echo 'Análisis Estático!'"
        withSonarQubeEnv('sonarqube') {
            sh 'chmod +x gradlew && ./gradlew sonarqube -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build'
        }
    }
    env.STAGE = 'Paso 3: Curl Springboot Gradle sleep 60'
    stage("$env.STAGE"){
        sh "gradle bootRun&"
        sh "sleep 60 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
    env.STAGE = 'Paso 4: Subir Nexus'
    stage("$env.STAGE"){
        nexusPublisher nexusInstanceId: 'nexus',
        nexusRepositoryId: 'devops-usach-nexus',
        packages: [
            [$class: 'MavenPackage',
                mavenAssetList: [
                    [classifier: '',
                    extension: '.jar',
                    filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar'
                ]
            ],
                mavenCoordinate: [
                    artifactId: 'DevOpsUsach2020',
                    groupId: 'com.devopsusach2020',
                    packaging: 'jar',
                    version: '0.0.1'
                ]
            ]
        ]
    }
    env.STAGE = 'Paso 5: Descargar Nexus'
    stage("$env.STAGE"){
        sh ' curl -X GET -u $NEXUS_USER:$NEXUS_PASSWORD "http://nexus:8081/repository/devops-usach-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
    }
    env.STAGE = 'Paso 6: Levantar Artefacto Jar'
    stage("$env.STAGE"){
        sh 'nohup bash java -jar DevOpsUsach2020-0.0.1.jar & >/dev/null'
    }
    env.STAGE = 'Paso 7: Testear Artefacto - Dormir(Esperar 60sg)'
    stage("$env.STAGE"){
        sh "sleep 60 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}
return this;