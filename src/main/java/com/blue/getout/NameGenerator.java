package com.blue.getout;

import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class NameGenerator {
    private static final String[] NOUNS = {"trampoline",
            "monster",
            "princess",
            "lion",
            "pirate",
            "boat",
            "ballerina",
            "superhero",
            "teacher",
            "chicken",
            "snake",
            "elf",
            "reindeer",
            "clown",
            "Santa",
            "dragonfly",
            "castle",
            "zoo",
            "monster truck",
            "Zamboni",
            "ice skate",
            "banana",
            "monkey",
            "mouse",
            "house",
            "hat",
            "sock",
            "soccer ball",
            "ankle",
            "flash card",
            "water bottle",
            "box",
            "box of tissues",
            "can of soda",
            "can of beans",
            "baby carrier",

            "hair bow",

            "skirt",

            "dress",

            "phone case",

            "bouncy ball",

            "nail file",

            "minivan",

            "tin can",

            "rubber ball",

            "rocking chair",

            "garbage bag",

            "scissors",

            "stapler",

            "staple remover",

            "lamp",

            "energy drink",

            "sprinkle",

            "picture frame",

            "stone",

            "rock",

            "brick",

            "swing",

            "slide",

            "chicken",

            "dog dish",

            "leash",

            "bone",

            "collar",

            "stuffed animal",

            "headphone",

            "earbud",

            "microphone",

            "laptop",

            "straw",

            "fence",

            "post",

            "stick",

            "tent",

            "Lego",

            "Lincoln Log",

            "jar of dirt",

            "box of pasta",

            "spaghetti noodle",

            "elbow noodle",

            "flour tortilla",

            "egg",

            "toilet bowl",

            "chair",

            "fern",

            "herb",

            "vitamin",

            "printer",

            "wet wipe",

            "diaper",

            "phone cord",

            "charger",

            "television",

            "DVD",

            "remote control",

            "video camera",

            "security camera",

            "chapstick",

            "makeup",

            "paperclip",

            "mixer",

            "blender",

            "butcher knife",

            "rainboot",

            "hiking boot",

            "snow boot",

            "raincoat",

            "mitten",

            "glove",

            "scarf",

            "claw",

            "notebook",

            "thermometer",

            "compass",

            "pen",

            "pencil",

            "book",

            "clipboard",

            "file",

            "folder",

            "filing cabinet",

            "sink",

            "taco",

            "pizza",

            "pepperoni",

            "sewing kit",

            "needle",

            "thread",

            "thimble",

            "sewing machine",

            "calendar",

            "tree",

            "Christmas tree",

            "flower",

            "flour",

            "sugar",

            "candle",

            "light bulb",

            "trophy",

            "lantern",

            "statue",

            "gate",

            "bassinet",

            "car seat",

            "car",

            "truck",

            "tractor",

            "dragon",

            "cot",

            "blanket",

            "tv tray",

            "mouse pad",

            "office chair",

            "laundry basket",

            "iron",

            "perfume",

            "smelling salt",

            "fire extinguisher",

            "fire truck",

            "dump truck",

            "police car",

            "airplane",

            "jet",

            "ticket",

            "cloud",

            "raindrop",

            "fingernail clipper",

            "cellphone",

            "landline",

            "teddy bear",

            "duster",

            "rubber ducky",

            "quilt",

            "heater",

            "air conditioner",

            "website",

            "video",

            "bottle of ink",

            "ink cartridge",

            "wand",

            "crystal ball",

            "magic carpet",

            "dollar bill",

            "coin",

            "measuring cup",

            "fork",

            "spoon",

            "whisk",

            "potato smasher",

            "egg timer",

            "bobby pin",

            "clamp",

            "curling iron",

            "straightener",

            "tablet",

            "doll",

            "hairbrush",

            "toothbrush",

            "shower curtain"};

    private static final String[] ADJECTIVES = {
            "happy",

            "sad",

            "angry",

            "crazy",

            "excited",

            "afraid",

            "lazy",

            "energetic",

            "strong",

            "smart",

            "dependent",

            "independent",

            "nice",

            "mean",

            "colorful",

            "boring",

            "funny",

            "cool",

            "pretty",

            "depressed",

            "fluffy",

            "furry",

            "gleeful",

            "glum",

            "frank",

            "goofy",

            "humble",

            "grumpy",

            "gruesome",

            "giddy",

            "gnarly",

            "grim",

            "hilarious",

            "hungry",

            "hyper",

            "inactive",

            "jealous",

            "lonely",

            "mad",

            "moody",

            "nervous",

            "overjoyed",

            "peaceful",

            "playful",

            "proud",

            "romantic",

            "safe",

            "silly",

            "sleepy",

            "snobby",

            "curious",

            "creative",

            "diligent",

            "dynamic",

            "devious",

            "discerning",

            "eloquent",

            "empathetic",

            "ethical",

            "fearless",

            "flawless",

            "stressed",

            "strong",

            "thrilled",

            "tired",

            "ugly",

            "weak",

            "whimsical",

            "wrongheadedly",

            "zealous",

            "groggy",

            "energetic",

            "comforting",

            "relaxing",

            "peaceful",

            "cheerful",

            "bright",

            "gorgeous",

            "lovely",

            "trustworthy",

            "loyal",

            "smart",

            "sweet",

            "tall",

            "short",

            "fat",

            "skinny",

            "tired",

            "smelly",

            "grumpy",

            "compassionate",

            "competitive",

            "confident",

            "cool",

            "courageous",

            "angry",

            "smiley",

            "brave",

            "hard",

            "adept",

            "adventurous",

            "eccentric",

            "eradic",

            "odd",

            "outlandish",

            "peculiar",

            "quiet",

            "quaint",

            "singular",

            "astute",

            "authentic",

            "bizarre",

            "blissful",

            "calculated",

            "candid",

            "strange",

            "loquacious",

            "dirty",

            "filthy",

            "terrible",

            "suberb",

            "great",

            "grateful",

            "fantastic",

            "bad",

            "bold",

            "soft",

            "tearful",

            "joyful",

            "intelligent",

            "dumb",

            "slow",

            "fast",

            "aromatic",

            "wonderful",

            "awestruck",

            "abnormal",

            "abundant",

            "accomplished",

            "adaptable",

            "resourceful",

            "reliable",

            "sensible",

            "sincere",

            "sympathetic",

            "motivated",

            "obejective",

            "organized",

            "passionate",

            "trustworthy",

            "polite",

            "political",

            "popular",

            "proactive",

            "inactive",

            "witty",

            "charming",

            "charismatic",

            "quirky",

            "radiant",

            "rational",

            "resourceful",

            "shameless",

            "serene",

            "beautiful",

            "brilliant",

            "careless",

            "emotional",

            "funny",

            "weepy",

            "gloomy",

            "impatient",

            "patient",

            "introverted",

            "extroverted",

            "jovial",

            "judicious",

            "keen",

            "knowledgeable",

            "wise",

            "learner",

            "loving",

            "level-headed",

            "magical",

            "methodical",

            "moral",

            "unique",

            "analytical",

            "arty",

            "assertive",

            "hysterical",

            "idealistic",

            "inspiring",

            "intense",

            "perceptive",

            "persevering",

            "playful",

            "pleasant",

            "skilled",

            "sociable"
    };
    private final Random random = new Random();
    public String generateRandomName(){
        String str = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String adjectiveUpperCase = str.substring(0, 1).toUpperCase() + str.substring(1);
         str = NOUNS[random.nextInt(NOUNS.length)];
        String nounUpperCase = str.substring(0, 1).toUpperCase() + str.substring(1);

        return adjectiveUpperCase+" "+nounUpperCase;
    }
}
