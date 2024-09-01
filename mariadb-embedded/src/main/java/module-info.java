module swdc.preset.mariadbembedded {

    requires java.sql;
    requires swdc.commons;
    requires swdc.presets.mariadb;
    requires org.bytedeco.javacpp;

    exports org.swdc.mariadb.embed;
    exports org.swdc.mariadb.embed.jdbc;
    exports org.swdc.mariadb.embed.jdbc.results;

    uses java.sql.Driver;
    provides java.sql.Driver with org.swdc.mariadb.embed.jdbc.EmbedMariaDBDriver;

}