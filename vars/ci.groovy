import utilities.*

def call(stages,compileTool){
    figlet  "CD"
    def listStagesOrder = [
        'compile': 'sCompile',
        'unitTest': 'sUnitTest',
        'sonar': 'sSonar',
        'nexusUpload': 'sNexusUpload',
        'gitCreateRelease': 'sGitCreateRelease'
    ]
    def arrayUtils = new array.arrayExtentions();
    def stagesArray = []
        stagesArray = arrayUtils.searchKeyInArray(stages, ";", listStagesOrder)

    if (stagesArray.isEmpty()) {
        echo 'El pipeline se ejecutará completo'
        allStages()
    } else {
        echo 'Stages a ejecutar :' + stages
        stagesArray.each{ stageFunction ->//variable as param
            echo 'Ejecutando ' + stageFunction
            "${stageFunction}"()
        }
    }
}

def allStages(){
    sCompile()
    sUnitTest()
    sNexusUpload()
    sGitCreateRelease()
}

def sCompile(){
    stage("compile"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
        switch(env.COMPILE_TOOL){
            case 'Maven':
                maven.call('compile')
            break;
            case 'Gradle':
                gradle.call('build')
            break;
        }
    }
}

def sUnitTest(){
    stage("unitTest"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
        switch(env.COMPILE_TOOL){
            case 'Maven':
                maven.call('test')
            break;
            case 'Gradle':
                gradle.call('build')
            break;
        }
    }
}

def sSonar(){
    stage("sonar"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
        switch(env.COMPILE_TOOL){
            case 'Maven':
                maven.call('sonar')
            break;
            case 'Gradle':
                gradle.call('sonar')
            break;
        }
    }
}

def sNexusUpload(){
    stage("nexusUpload"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
        switch(env.COMPILE_TOOL){
            case 'Maven':
                maven.call('upload_nexus')
            break;
            case 'Gradle':
                gradle.call('upload_nexus')
            break;
        }
    }
}

def sGitCreateRelease(){
    stage("gitCreateRelease"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
    }
}