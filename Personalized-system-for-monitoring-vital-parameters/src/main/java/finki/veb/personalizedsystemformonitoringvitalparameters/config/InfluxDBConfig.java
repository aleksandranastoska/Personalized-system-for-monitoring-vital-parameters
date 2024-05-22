package finki.veb.personalizedsystemformonitoringvitalparameters.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url}")
    private String influxdbUrl;

    @Value("${influxdb.token}")
    private String influxdbToken;

    @Value("${influxdb.org}")
    private String influxdbOrg;

    @Value("${influxdb.bucket}")
    private String influxdbBucket;

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(influxdbUrl, influxdbToken.toCharArray(), influxdbOrg, influxdbBucket);
    }
}