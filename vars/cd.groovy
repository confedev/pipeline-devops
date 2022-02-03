import utilities.*

def call(stages){
    figlet  "CD"
    def listStagesOrder = [
        'gitDiff': 'sGitDiff',
        'nexusDownload': 'sNexusDownload',
        'run': 'sRun',
        'test': 'sTest',
        'gitMergeMaster': 'sGitMergeMaster',
        'gitMergeDevelop': 'sGitMergeDevelop',
        'gitTagMaster': 'sGitTagMaster'
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
    sGitDiff()
    sNexusDownload()
    sRun()
    sTest()
    sGitMergeMaster()
    sGitMergeDevelop()
    sGitTagMaster()
}

def sGitDiff(){
    stage("gitDiff"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
    }
}

def sNexusDownload(){
    stage("nexusDownload"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
        switch(env.COMPILE_TOOL){
            case 'Maven':
                maven.call('download_nexus')
            break;
            case 'Gradle':
                gradle.call('download_nexus')
            break;
        }
    }
}

def sRun(){
    stage("run"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
        switch(env.COMPILE_TOOL){
            case 'Maven':
                maven.call('run_artifact')
            break;
            case 'Gradle':
                gradle.call('run_artifact')
            break;
        }
    }
}

def sTest(){
    stage("test"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
        switch(env.COMPILE_TOOL){
            case 'Maven':
                maven.call('test_artifact')
            break;
            case 'Gradle':
                gradle.call('test_artifact')
            break;
        }
    }
}

def gitMergeMaster(){
    stage("sGitMergeMaster"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
    }
}

def gitMergeDevelop(){
    stage("sGitMergeDevelop"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
    }
}

def gitTagMaster(){
    stage("sGitTagMaster"){
        env.STAGE = env.STAGE_NAME
        sh "echo 'Ejecutando: $STAGE_NAME!'"
    }
}