package pac.app.controller;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.apache.client.impl.ApacheConnectionManagerFactory;
import com.amazonaws.http.apache.client.impl.ApacheHttpClientFactory;
import com.amazonaws.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.amazonaws.http.client.ConnectionManagerFactory;
import com.amazonaws.http.client.HttpClientFactory;
import com.amazonaws.http.conn.ClientConnectionManagerFactory;
import com.amazonaws.http.settings.HttpClientSettings;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;

import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Controller("/")
public class MainController {

    private static final Log LOG = LogFactory.getLog(MainController.class);
    private ObjectMapper mapper = new ObjectMapper()
            .disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
            .disable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
            .enable(JsonParser.Feature.ALLOW_COMMENTS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static final HttpClientFactory<ConnectionManagerAwareHttpClient> httpClientFactory = new
            ApacheHttpClientFactory();

    private HttpClient httpClient;

    private static DynamoDBMapper dbMapper = null;
    private static AmazonDynamoDB amazonDynamoDBClient = null;

    private static Table table = null;

    public MainController() {
        this.httpClient = httpClientFactory.create(HttpClientSettings.adapt(new ClientConfiguration()));
        LOG.info("init");
        final ConnectionManagerFactory<HttpClientConnectionManager> cmFactory = new ApacheConnectionManagerFactory();
        final HttpClientConnectionManager cm = cmFactory.create(HttpClientSettings.adapt(new ClientConfiguration()));
        ClientConnectionManagerFactory.wrap(cm);
    }

    @Get("/ping")
    public String index() throws IOException {
        LOG.info("Local Test");

        LOG.info("Local Test2");
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();

        dbMapper = new DynamoDBMapper(amazonDynamoDBClient);
        LOG.info("Local Test3");
        table = new DynamoDB(amazonDynamoDBClient).getTable("pac_all");
        LOG.info("Local Test4");
        Item item = table.getItem("pk","001","sk","0002");
        LOG.info("Local Test5");
        String base_janCode = item.get("jan").toString();
        String base_promotionDesc = item.get("PromotionDesc").toString();
        LOG.info(base_janCode);
        LOG.info(base_promotionDesc);
        return "{\"jan\":\"" + base_janCode + "\",\"point\":\"" + base_promotionDesc + "\"}";
    }
    @Get
    public String test() throws IOException {
        LOG.info("Local Test");
        LOG.info("Local Test2");
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();

        dbMapper = new DynamoDBMapper(amazonDynamoDBClient);
        LOG.info("Local Test3");
        table = new DynamoDB(amazonDynamoDBClient).getTable("pac_all");
        LOG.info("Local Test4");
        Item item = table.getItem("pk","001","sk","0002");
        LOG.info("Local Test5");
        String base_rank = item.get("rank").toString();
        String base_type = item.get("type").toString();
        LOG.info(base_type);
        return "{\"jan\":\"" + base_rank + "\",\"point\":\"" + base_type + "\"}";

    }


    @Get("/go/{pet}")
    public String findPrimesBelow(String pet) {
        return pet;
    }
    // @Get("/go/{jan}")
    // public String findAllItems(@pathvariable("jan") String jan) {
    //     amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
    //             .withCredentials(new DefaultAWSCredentialsProviderChain())
    //             .withRegion(Regions.US_EAST_1).build();
    //             dbMapper = new DynamoDBMapper(amazonDynamoDBClient);
    //             table = new DynamoDB(amazonDynamoDBClient).getTable("pac_all");
    //             Item item = table.getItem(jan);
    //             if(item!=null)
    //             {
    //                 String base_rank = item.get("rank").toString();
    //                 String base_type = item.get("type").toString();
    //                 String base_jan = item.get("jan").toString();
    //                 String base_promotionDesc = item.get("PromotionDesc").toString();
    //             } 
    //             LOG.info(base_type);
    //             LOG.info(base_rank);
    //             LOG.info(base_jan);
    //             LOG.info(base_promotionDesc);
    //             return "{\"jan\":\"" + base_rank + "\",\"point\":\"" + base_type + "\"" +base_jan+"/"+base_promotionDesc+"\"}";
        
    // }
    @Get("/test")
    public String ts() throws IOException {
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();

        LOG.info("Local Test3");

        HashMap<String, Condition> scanFilter = new HashMap<>();

        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS("123456789012"));

        scanFilter.put("jan", condition);
        ScanRequest scanRequest = new ScanRequest("pac_all").withScanFilter(scanFilter);
        ScanResult scanResult = amazonDynamoDBClient.scan(scanRequest);
        List<java.util.Map<String, AttributeValue>> aa = scanResult.getItems();
        LOG.info(aa.size());

        for (int i = 0; i < aa.size(); i++) {
            java.util.Map<String, AttributeValue> bb = aa.get(i);

            Iterator<String> iterator = bb.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                AttributeValue cc = bb.get(key);
                LOG.info(key);
                LOG.info(cc.toString());
            }
        }
        return "murugan";
    }
}
