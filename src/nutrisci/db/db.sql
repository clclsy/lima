CREATE TABLE ConversionFactors (
    food_id INT,
    measure_id INT,
    conversion_factor DOUBLE,
    date_of_entry DATE,
    PRIMARY KEY (food_id, measure_id)
);

CREATE TABLE FoodGroups (
    food_group_id INT PRIMARY KEY,
    food_group_code INT,
    name_en VARCHAR(100),
    name_fr VARCHAR(100)
);

CREATE TABLE FoodDescriptions (
    food_id INT PRIMARY KEY,
    food_code INT,
    food_group_id INT,
    food_source_id INT,
    description_en VARCHAR(255),
    description_fr VARCHAR(255),
    date_of_entry DATE,
    date_of_publication DATE,
    country_code VARCHAR(20),
    scientific_name VARCHAR(255)
);

CREATE TABLE FoodSources (
    food_source_id INT PRIMARY KEY,
    source_code INT,
    description_en VARCHAR(255),
    description_fr VARCHAR(255)
);

CREATE TABLE Measures (
    measure_id INT PRIMARY KEY,
    description_en VARCHAR(100),
    description_fr VARCHAR(100)
);

CREATE TABLE NutrientData (
    food_id INT,
    nutrient_id INT,
    nutrient_value DOUBLE,
    standard_error DOUBLE,
    observations INT,
    nutrient_source_id INT,
    date_of_entry DATE,
    PRIMARY KEY (food_id, nutrient_id)
);

CREATE TABLE Nutrients (
    nutrient_id INT PRIMARY KEY,
    nutrient_code INT,
    symbol VARCHAR(10),
    unit VARCHAR(10),
    name_en VARCHAR(100),
    name_fr VARCHAR(100),
    tagname VARCHAR(20),
    decimals INT
);

CREATE TABLE NutrientSources (
    nutrient_source_id INT PRIMARY KEY,
    source_code INT,
    description_en VARCHAR(255),
    description_fr VARCHAR(255)
);

CREATE TABLE RefuseAmounts (
    food_id INT,
    refuse_id INT,
    refuse_amount DOUBLE,
    date_of_entry DATE,
    PRIMARY KEY (food_id, refuse_id)
);

CREATE TABLE RefuseDescriptions (
    refuse_id INT PRIMARY KEY,
    description_en VARCHAR(255),
    description_fr VARCHAR(255)
);

CREATE TABLE Yields (
    food_id INT,
    yield_id INT,
    yield_amount DOUBLE,
    date_of_entry DATE,
    PRIMARY KEY (food_id, yield_id)
);

CREATE TABLE YieldDescriptions (
    yield_id INT PRIMARY KEY,
    description_en VARCHAR(255),
    description_fr VARCHAR(255)
);
