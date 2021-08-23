package com.instagram.injector;

import com.instagram.injector.entity.Instagram;
import com.instagram.injector.repository.InstagramRepository;
import com.instagram.injector.service.InstagramService;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class InstagramInjectorApplication implements CommandLineRunner {

    @Autowired
    private InstagramService instagramService;

    @Autowired
    private InstagramRepository instagramRepository;

    @Value("${src.folder.pictures}") private String picturesFolder;


    public static void main(String[] args) {
        SpringApplication.run(InstagramInjectorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        WebClient web = WebClient.create("https://www.instagram.com/graphql/query");
        JSONObject variables = new JSONObject();
        variables.put("tag_name","cbrebestoffice");
        variables.put("first","10");

        String data = web.get()
                .uri(builder -> builder
                        .queryParam("query_hash", "{1}")
                        .queryParam("variables", "{2}").build("298b92c8d7cad703f7565aa892ede943", variables))
                .retrieve().bodyToMono(String.class).block();


        JSONObject jo = new JSONObject(data);
        jo  = jo.getJSONObject("data");
        jo = jo.getJSONObject("hashtag");
        jo = jo.getJSONObject("edge_hashtag_to_media");
        JSONArray edges = jo.getJSONArray("edges");

        List<Instagram> postInstagrams = new ArrayList<>();


        for (int i = 0; i < edges.length(); i++){

            JSONObject objects = edges.getJSONObject(i);
            Instagram postInstagram = new Instagram();

            postInstagram.setId((long) i);
            postInstagram.setPostId(objects.getJSONObject("node").getLong("id"));
            postInstagram.setDisplayUrl(objects.getJSONObject("node").getString("display_url"));

            postInstagram.setDescription(objects.getJSONObject("node").getJSONObject("edge_media_to_caption").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("text"));


            postInstagrams.add(postInstagram);
        }

        // TODO get stream filter who contains #cbrebestoffice
        postInstagrams = postInstagrams.stream().filter(postInstagram -> postInstagram.getDescription().toLowerCase().contains("#notyourordinaryoffice")).collect(Collectors.toList());
//        postInstagrams = postInstagrams.stream().filter(postInstagram -> postInstagram.getDescription().toLowerCase().contains("#cbrebestoffice")).collect(Collectors.toList());

        for (Instagram instagram : postInstagrams){
            // TODO Execute si il n'existe pas
            if (!instagramRepository.existsByPostId(instagram.getPostId())){
                BufferedImage image;
                String path = instagram.getPostId() + ".jpg";
                instagram.setPath(path);
                image = ImageIO.read(new URL(instagram.getDisplayUrl()));
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                FileUtils.copyURLToFile(new URL(instagram.getDisplayUrl()),
                        new File(picturesFolder+path));
                instagramService.create(instagram);
            }
        }
    }
}
