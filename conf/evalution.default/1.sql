CREATE TABLE "Example" (
    "id" SERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR NOT NULL
);
ALTER TABLE "Example" add column "tel" varchar null;
ALTER TABLE "Example" add column "age" varchar null;
ALTER TABLE "Example" add column "address" varchar null;