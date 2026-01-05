module swdc.presets.mariadb {

    requires transitive org.bytedeco.javacpp;

    exports org.swdc.mariadb.core;
    exports org.swdc.mariadb.core.mysql;
    exports org.swdc.mariadb.core.com;
    exports org.swdc.mariadb.core.global;


}