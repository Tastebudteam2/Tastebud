package com.tastebud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.util.*;


public class TastebudDynamicFunction {

    private static ObjectMapper mapper = new ObjectMapper();
/**
     * Generates a recipe JSON object based on the given dish name.
     * @param dishName The name of the dish.
     * @return A JSON object representing the recipe.
     */
    private static JsonNode getRecipe(String DishName) {
        // Create a JSON object for the recipe
        ObjectNode response = mapper.createObjectNode();
        response.put("Dish Name: ", DishName);
        response.set("ingredients", generateIngredients());
        response.set("instructions", generateInstructions());
             
        return response;
    }

    /**
     * Generates a list of ingredients with random names and quantities.
     * @return A JSON array representing the list of ingredients.
     */
    private static ArrayNode generateIngredients() {
        // Create a JSON array for ingredients
        ArrayNode ingredients = mapper.createArrayNode();

        // Generate a random number of ingredients (between 3 and 20)
        int numberOfIngredients = getRandomNumber(3, 20);

        // Generate random ingredient data
        for (int i = 0; i < numberOfIngredients; i++) {
            ObjectNode ingredient = mapper.createObjectNode();
            ingredient.put("name", "Ingredient" + (i + 1));
            ingredient.put("quantity", getRandomNumber(1, 100) + " grams");
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    /**
     * Generates a list of cooking instructions with random steps.
     * @return A JSON array representing the list of instructions.
     */

    private static ArrayNode generateInstructions() {
        ArrayNode instructions = mapper.createArrayNode();

        // Generate a random number of instructions (between 3 and 20)
        int numberOfInstructions = getRandomNumber(3, 20);

        for (int i = 0; i < numberOfInstructions; i++) {
            instructions.add("Step " + (i + 1) + ": Do something");
        }

        return instructions;
    }

    private static int getRandomNumber(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    
    public static void main(String... args) {
        String token = System.getenv("OPEN_AI_KEY");
        OpenAiService service = new OpenAiService(token);

         // Define the 'get_recipe' function for the Chat API
        ChatFunctionDynamic recipeFunction = ChatFunctionDynamic.builder()
                .name("get_recipe")
                .description("Get the recipe of the given dish")
                .addProperty(ChatFunctionProperty.builder()
                        .name("DishName")
                        .type("string")
                        .description("Name of dish, for example: Moussaka")
                        .build())
                .addProperty(ChatFunctionProperty.builder()
                        .name("ingredients")
                        .type("list_of_string")
                        .description("Ingredients for the dish")
                        .build())
                .addProperty(ChatFunctionProperty.builder()
                        .name("instructions")
                        .type("list_of_string")
                        .description("Cooking instructions for the dish")
                        .build())
                .build();

        // Initialize a list of chat messages
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that provides recipes.");
        messages.add(systemMessage);

        // Get the first user query
        System.out.print("First Query: ");
        Scanner scanner = new Scanner(System.in);
        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), scanner.nextLine());
        messages.add(firstMsg);

        // Process user queries and provide responses
        while (true) {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-3.5-turbo-0613")
                    .messages(messages)
                    .functions(Collections.singletonList(recipeFunction))
                    .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                    .n(1)
                    .maxTokens(500)
                    .logitBias(new HashMap<>())
                    .build();
            ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
            messages.add(responseMessage);

            ChatFunctionCall functionCall = responseMessage.getFunctionCall();
            if (functionCall != null) {
                // If the response is a 'get_recipe' function call, generate and add a recipe message
                if (functionCall.getName().equals("get_recipe")) {
                    String DishName = functionCall.getArguments().get("DishName").asText();
                    JsonNode recipe = getRecipe(DishName);
                    ChatMessage recipeMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), recipe.toString(), "get_recipe");
                    messages.add(recipeMessage);
                    continue;
                }
            } else {
                // If the response is not a function call, display the response and get the next user query
                System.out.println("Response: " + responseMessage.getContent());
                System.out.print("Next Query: ");
                String nextLine = scanner.nextLine();
                if (nextLine.equalsIgnoreCase("exit")) {
                    System.exit(0);
                }
                messages.add(new ChatMessage(ChatMessageRole.USER.value(), nextLine));
        }
    }
    
}
}
