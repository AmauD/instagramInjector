package com.instagram.injector;

import com.instagram.entity.Instagram;
import com.instagram.service.InstagramService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppStartupRunner implements ApplicationRunner {

    @Autowired InstagramService instagramService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        runInjector();
    }

    private void runInjector() {

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

            postInstagram.setId_post(objects.getJSONObject("node").getLong("id"));
            postInstagram.setDisplay_url(objects.getJSONObject("node").getString("display_url"));
            postInstagram.setDescription(objects.getJSONObject("node")
                    .getJSONObject("edge_media_to_caption")
                    .getJSONArray("edges")
                    .getJSONObject(0)
                    .getJSONObject("node")
                    .getString("text"));


            postInstagrams.add(postInstagram);
        }

        // TODO remove stream filter who contains #cbrebestoffice
        postInstagrams.stream().filter(postInstagram -> postInstagram.getDescription().toLowerCase().contains("@cbrebelgium")).collect(Collectors.toList());
        postInstagrams.stream().filter(postInstagram -> postInstagram.getDescription().toLowerCase().contains("#cbrebestoffice")).collect(Collectors.toList());



        // Todo Insérez list dans la base de données
        // for loop and insert 1 by 1

        for (int i = 0; i < postInstagrams.size(); i++){
            Instagram postInstagram = postInstagrams.get(i);
            postInstagram.setId((long) i);
            postInstagram.getId();
            postInstagram.getId_post();
            postInstagram.setPost_date(postInstagram.getPost_date());
            postInstagram.getPost_date();
            postInstagram.getDescription();
            postInstagram.getDisplay_url();


            instagramService.create(postInstagram);
        }
    }
}