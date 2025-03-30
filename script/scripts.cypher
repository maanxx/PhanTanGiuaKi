// Cau 1:
//////////// - tao constraint unique
CREATE CONSTRAINT unique_doctor_id FOR (doc:Doctor) REQUIRE doc.doctor_id IS UNIQUE;
CREATE CONSTRAINT unique_department_id FOR (d:Department) REQUIRE d.dept_id IS UNIQUE;
CREATE CONSTRAINT unique_patient_id FOR (p:Patient) REQUIRE p.patient_id IS UNIQUE;

SHOW CONSTRAINT

//////////// - Load data csv

// Load department
LOAD CSV WITH HEADERS FROM "file:///departments.csv" AS row
WITH row WHERE row.id IS NOT NULL
MERGE (d:Department {dept_id: row.id})
SET
d.name = row.name,
d.location = row.location;

// Load doctor
LOAD CSV WITH HEADERS FROM "file:///doctors.csv" AS row
WITH row WHERE row.ID IS NOT NULL
MERGE (doc:Doctor {doctor_id: row.ID})
SET
doc.name = row.Name,
doc.phone = row.Phone,
doc.speciality = row.Speciality;

// Load patient
LOAD CSV WITH HEADERS FROM "file:///patients.csv" AS row
WITH row WHERE row.ID IS NOT NULL
MERGE (p:Patient {patient_id: row.ID})
SET
p.name = row.Name,
p.phone = row.Phone,
p.gender = row.Gender,
p.date_of_birth = date(row.DateOfBirth),
p.address = row.Address;

//Find patients born in 1990
MATCH (p:Patient) WHERE p.date_of_birth.year=1990 RETURN p;

//////// Merge
// merge doctor -> BELONG_TO -> department
LOAD CSV WITH HEADERS FROM "file:///doctors.csv" AS row
WITH row WHERE row.ID IS NOT NULL AND row.DepartmentID IS NOT NULL
MATCH (doc:Doctor {doctor_id: row.ID})
MATCH (d:Department {dept_id: row.DepartmentID})
MERGE (doc)-[:BELONG_TO]->(d);

/// merge patient -> BE_TREATED -> doctor
LOAD CSV WITH HEADERS FROM "file:///treatments.csv" AS row
WITH row WHERE row.DoctorID IS NOT NULL AND row.PatientID IS NOT NULL
MATCH (p:Patient {patient_id: row.PatientID})
MATCH (doc:Doctor {doctor_id: row.DoctorID})
MERGE (p)-[:BE_TREATED {
start_date: date(row.StartDate),
end_date: date(row.EndDate),
diagnosis: row.Diagnosis
}]->(doc);

// delete table
MATCH (n:Doctor) DETACH DELETE n;

// delete relationship
MATCH (:Doctor)-[r:BELONG_TO]->(:Department)
DELETE r;

// FULL-TEXT
CREATE FULLTEXT INDEX doctor_speciality_index FOR (doc:Doctor) ON EACH [doc.speciality];
//# Strip: Tìm bác sĩ khi có mã id
MATCH (d:Doctor {doctor_id: $doctor_id}) RETURN d;

//# Script Cau a: Tạo một bác sĩ mới
CREATE (d:Doctor {doctor_id: $doctor_id, name: $name, phone: $phone, speciality: $speciality}) RETURN d

//# Script Cau b: Thống kê số bác sỹ theo từng chuyên khoa (speciality) của một khoa (department)
//nào đó khi biết tên khoa.
//+ getNoOfDoctorsBySpeciality (departmentName: String) : Map<String, Long>
MATCH (d: Department{name: "Family Medicine"})<-[r:BELONG_TO]-(doc: Doctor)
RETURN doc.speciality as speciality, COUNT(doc) as noOfDoctors
//# Stript
MATCH (d: Department{name: $departmentName})<-[r:BELONG_TO]-(doc: Doctor)
RETURN doc.speciality as speciality, COUNT(doc) as noOfDoctors
//# Script Cau c:  Tìm bác sĩ theo chuyên khoa (sử dụng FULL TEXT SEARCH)
//# Tạo FULL TEXT INDEX trên thuộc tính speciality của Doctor

// run firstly
CREATE FULLTEXT INDEX txt_index_speciality FOR (doc: Doctor) ON EACH [doc.speciality];
// after running
CALL db.index.fulltext.queryNodes("txt_index_speciality", "Sports") YIELD node, score RETURN node;


//# Script Cau d: Cập nhật nhật lại chuẩn đoán của bệnh nhân
MATCH (p:Patient {patient_id: $patientId})-[r:BE_TREATED]->(d:Doctor {doctor_id: $doctorId})
WHERE r.end_date IS NULL
SET r.diagnosis = $diagnosis

// r.end_date = date();

//Add some relationships between patients and doctors, and set the start_date property
MATCH (p:Patient {patient_id: "PT003"}), (d:Doctor {doctor_id: "DR.001"})
CREATE (p)-[:BE_TREATED {start_date: date("2025-03-01")}]->(d);

//Update the diagnosis and end_date properties of the relationship
MATCH (p:Patient {patient_id: "PT003"})-[r:BE_TREATED]->(d:Doctor {doctor_id: "DR.001"})
WHERE r.end_date IS NULL
SET r.diagnosis = "abc";