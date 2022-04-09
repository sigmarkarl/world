plugins {
    id("java")
    id("org.openjfx.javafxplugin")
}

javafx {
    version = "17.0.2"
    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web")
}

dependencies {
    implementation(project(":TreeDraw"))
    implementation(group = "org.apache.poi", name = "poi", version = "4.1.2")
    implementation(group = "org.apache.poi", name = "poi-ooxml", version = "4.1.2")
    implementation(group = "org.ejml", name = "ejml-all", version = "0.41")
    implementation(group = "com.github.samtools", name = "htsjdk", version = "2.24.0")
    implementation(group = "com.googlecode.json-simple", name = "json-simple", version = "1.1")
    implementation(group = "org.json", name = "json", version = "20190722")
    implementation(group = "org.apache.spark", name = "spark-core_2.13", version = "3.2.1")
    implementation(group = "org.apache.spark", name = "spark-mllib_2.13", version = "3.2.1")
    implementation(group = "org.apache.spark", name = "spark-kubernetes_2.13", version = "3.2.1")
    //implementation(group = "org.scalanlp", name = "breeze_2.13", version = "1.1")

    implementation("org.java-websocket:Java-WebSocket:1.5.2")
}