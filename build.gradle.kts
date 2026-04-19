plugins {
    id("java")
    signing
    `maven-publish`
    id("com.gradleup.nmcp") version "0.0.8"
    `java-library`
    id("io.freefair.lombok") version "9.2.0"
}

group = "dev.amraleth"
version = "1.6.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api("org.jetbrains:annotations:26.0.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    options {
        this as StandardJavadocDocletOptions
        tags(
            "apiNote:a:API Note:",
        )
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("JBlob")
                description.set("A blob of common java functions and utils")
                url.set("https://github.com/amraleth/jblob")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("amraleth")
                        name.set("Patrick Vollandt")
                        email.set("patrick@vollandt.dev")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/amraleth/jblob.git")
                    developerConnection.set("scm:git:ssh://github.com/amraleth/jblob.git")
                    url.set("https://github.com/amraleth/jblob")
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

nmcp {
    publish("mavenJava") {
        username = providers.gradleProperty("sonatypeUsername").get()
        password = providers.gradleProperty("sonatypePassword").get()
        publicationType = "AUTOMATIC"
    }
}