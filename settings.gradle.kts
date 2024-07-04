pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")

            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven (url ="https://repository.map.naver.com/archive/maven")
        maven(url ="https://devrepo.kakao.com/nexus/content/groups/public/")
    //maven(url="https://naver.jfrog.io/artifactory/maven/")
    }
}

rootProject.name = "tab3"
include(":app")
 