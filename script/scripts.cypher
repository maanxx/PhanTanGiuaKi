// Cau 1:
//////////// - tao constraint unique
CREATE CONSTRAINT uni_doctor_id FOR (doc:Doctor) REQUIRE doc.id IS UNIQUE;
CREATE CONSTRAINT uni_department_id FOR (d:Department) REQUIRE d.ID IS UNIQUE;
CREATE CONSTRAINT uni_patient_id FOR (p:Patient) REQUIRE p.ID IS UNIQUE;

SHOW CONSTRAINT

//////////// - Load data csv

// Load department
LOAD CSV WITH HEADERS FROM "file:///departments.csv" AS row
WITH row WHERE row.id IS NOT NULL
MERGE (d:Department {department_id: row.id})
SET
d.name = row.name,
d.location = row.location;
// load patient
LOAD CSV WITH HEADERS FROM "file:///patients.csv" AS row
WITH row WHERE row.ID IS NOT NULL
MERGE (p:Patient {patient_id: row.ID})
SET
p.Name = row.Name,
p.Phone = row.Phone,
p.Gender = row.Gender,
p.DateOfBirth = row.DateOfBirth,
p.Address = row.Address;
// load doctor
LOAD CSV WITH HEADERS FROM "file:///doctors.csv" AS row
WITH row WHERE row.ID IS NOT NULL
MERGE (doc:Doctor {doctor_id: row.ID})
SET
doc.Name = row.Name,
doc.Phone = row.Phone,
doc.Speciality = row.Speciality;

//////// Merge
// merge doctor -> BELONG_TO -> department
LOAD CSV WITH HEADERS FROM "file:///doctors.csv" AS row
WITH row WHERE row.ID IS NOT NULL AND row.DepartmentID IS NOT NULL
MATCH (doc:Doctor {doctor_id: row.ID})
MATCH (d:Department {department_id: row.DepartmentID})
WHERE d IS NOT NULL
MERGE (doc)-[:BELONG_TO]->(d);

/// merge patient -> BE_TREATED -> doctor
LOAD CSV WITH HEADERS FROM "file:///treatments.csv" AS row
WITH row WHERE row.DoctorID IS NOT NULL AND row.PatientID IS NOT NULL
MATCH (p:Patient {patient_id: row.PatientID})
MATCH (doc:Doctor {doctor_id: row.DoctorID})
WHERE p IS NOT NULL AND doc IS NOT NULL
MERGE (p)-[:BE_TREATED {
StartDate: date(row.StartDate),
EndDate: date(row.EndDate),
Diagnosis: row.Diagnosis
}]->(doc);


// delete table
MATCH (n:Doctor) DETACH DELETE n;

// delete relationship
MATCH (:Doctor)-[r:BELONG_TO]->(:Department)
DELETE r;

// FULL-TEXT
CREATE FULLTEXT INDEX doctor_speciality_index
FOR (doc:Doctor)
ON EACH [doc.Speciality];