CREATE TABLE "documents" (
    "id" SERIAL NOT NULL PRIMARY KEY,
    "createAt" TIMESTAMP NULL,
    "section" VARCHAR NULL,
    "documentType" VARCHAR NULL,
    "subDocumentType" VARCHAR NULL
);
DROP TABLE "documents";
ALTER TABLE "documents" ADD COLUMN "group" VARCHAR NULL;
ALTER TABLE "documents" ADD COLUMN "executive" VARCHAR NULL;