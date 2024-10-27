package imf.virtualpet.virtualpet_secured.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Random;

@Data
@Document
public class VirtualPet {
    @Id
    private String id;
    @Field("Name")
    private String name;
    @Field("PetType")
    private PetType petType;
    @Field("Colour")
    private String colour;
    @Field("Hunger")
    private boolean isHungry;
    @Field("Happiness")
    private boolean isHappy;

    public VirtualPet(String name, PetType petType, String colour) {
        this.name = name;
        this.petType = petType;
        this.colour =  colour;
        this.isHungry = getRandomBool();
        this.isHappy = getRandomBool();
    }

    public static boolean getRandomBool() {
        Random randomNumber = new Random();
        int myRandNumber = randomNumber.nextInt(2);
        return myRandNumber == 1;
    }

}
