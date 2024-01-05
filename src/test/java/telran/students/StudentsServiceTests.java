package telran.students;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import telran.exceptions.NotFoundException;
import telran.students.dto.Mark;
import telran.students.dto.Student;
import telran.students.model.StudentDoc;
import telran.students.repo.StudentRepo;
import telran.students.service.StudentsService;

@SpringBootTest
class StudentsServiceTests {
    @Autowired
    StudentsService studentsService;

    @MockBean
    StudentRepo studentRepo;

    @Autowired
    DbTestCreation dbCreation;

    @BeforeEach
    void setUp() {
        dbCreation.createDB();
    }

    @Test
    void addStudentTest() {
        Student studentToAdd = new Student(8L, "New Student", "123-4567890");
        when(studentRepo.existsById(8L)).thenReturn(false);

        Student addedStudent = studentsService.addStudent(studentToAdd);

        assertNotNull(addedStudent);
        assertEquals(studentToAdd.id(), addedStudent.id());
        assertEquals(studentToAdd.name(), addedStudent.name());
        assertEquals(studentToAdd.phone(), addedStudent.phone());
        verify(studentRepo, times(1)).save(any(StudentDoc.class));
    }

    @Test
    void addExistingStudentTest() {
        Student existingStudent = dbCreation.students[0];
        when(studentRepo.existsById(existingStudent.id())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> studentsService.addStudent(existingStudent));

        verify(studentRepo, never()).save(any(StudentDoc.class));
    }

    @Test
    void updatePhoneTest() {
        long studentId = 1L;
        String newPhone = "987-6543210";
        StudentDoc studentDoc = dbCreation.indexToStudent(0);
        when(studentRepo.findById(studentId)).thenReturn(java.util.Optional.of(studentDoc));

        Student updatedStudent = studentsService.updatePhone(studentId, newPhone);

        assertNotNull(updatedStudent);
        assertEquals(studentDoc.getId(), updatedStudent.id());
        assertEquals(studentDoc.getName(), updatedStudent.name());
        assertEquals(newPhone, updatedStudent.phone());
        verify(studentRepo, times(1)).save(any(StudentDoc.class));
    }
    
    @Test
    void removeExistingStudentTest() {
        long studentId = 1L;
        StudentDoc studentDoc = dbCreation.indexToStudent(0);
        when(studentRepo.findStudentNoMarks(studentId)).thenReturn(studentDoc);

        Student removedStudent = studentsService.removeStudent(studentId);

        assertNotNull(removedStudent);
        assertEquals(studentDoc.getId(), removedStudent.id());
        assertEquals(studentDoc.getName(), removedStudent.name());
        verify(studentRepo, times(1)).deleteById(studentId);
    }

    @Test
    void removeNonExistingStudentTest() {
        long nonExistingStudentId = 10L;
        when(studentRepo.findStudentNoMarks(nonExistingStudentId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> studentsService.removeStudent(nonExistingStudentId));

        verify(studentRepo, never()).deleteById(nonExistingStudentId);
    }

    @Test // class throws a NotFoundException when the student is not found
    void getMarksTest() {
        long studentId = 1L;
        Mark[] marksActual = studentsService.getMarks(studentId).toArray(Mark[]::new);
        Mark[] marksExpected = dbCreation.getStudentMarks(studentId);

        assertArrayEquals(marksExpected, marksActual);
    }

    @Test
    void getMarksTest1() {
        long nonExistingStudentId = 10L;
        
        // Mocking the behavior of findStudentMarks to return null for a non-existing student
        when(studentRepo.findStudentMarks(nonExistingStudentId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> studentsService.getMarks(nonExistingStudentId));

        verify(studentRepo, never()).save(any());

    }


    
    @Test
    void addMarkToNonExistingStudentTest() {
        long nonExistingStudentId = 10L;
        Mark markToAdd = new Mark("NonExistentSubject", LocalDate.now(), 80);

        assertThrows(NotFoundException.class, () -> studentsService.addMark(nonExistingStudentId, markToAdd));

        verify(studentRepo, never()).save(any(StudentDoc.class));
    }

    @Test
    void updatePhoneForNonExistingStudentTest() {
        long nonExistingStudentId = 10L;
        String newPhone = "987-6543210";

        assertThrows(NotFoundException.class, () -> studentsService.updatePhone(nonExistingStudentId, newPhone));

        verify(studentRepo, never()).save(any(StudentDoc.class));
    }
}




//package telran.students;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import telran.students.dto.Mark;
//import telran.students.service.StudentsService;
//@SpringBootTest
//class StudentsServiceTests {
//	@Autowired
//StudentsService studentsService;
//	@Autowired
//	DbTestCreation dbCreation;
//	@BeforeEach
//	void setUp() {
//		dbCreation.createDB();
//	}
//	@Test
//	void getMarksTest() {
//		Mark[] marksActual = studentsService.getMarks(1).toArray(Mark[]::new);
//		Mark[] marksExpected = dbCreation.getStudentMarks(1);
//		assertArrayEquals(marksExpected, marksActual);
//	}
//
//}
