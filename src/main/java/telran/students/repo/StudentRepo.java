package telran.students.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import telran.students.dto.IdName;
import telran.students.dto.IdNamePhone;
import telran.students.dto.MarksOnly;
import telran.students.model.StudentDoc;

public interface StudentRepo extends MongoRepository<StudentDoc, Long> {
	@Query(value="{id:?0}", fields = "{marks:1, id:0}")
	StudentDoc findStudentMarks(long id);
	/***************************************************/
	@Query(value="{id:?0}", fields = "{id:1, name:1, phone:1}")
	StudentDoc findStudentNoMarks(long id);
	/**********************************************/
	IdName findByPhone(String phone);
	/*********************************************/
	List<IdNamePhone> findByPhoneRegex(String string);
	/*****************************************************/
	@Query(value="{$and:[{marks: {$elemMatch:{score:{$gt:?0}}}}, {marks: {$not:{$elemMatch:{score:{$lte:?0}}}}}]}")
	List<IdNamePhone> findByGoodMarks(int thresholdScore);
	/***************************************************************/
	@Query(value="{$expr:{$lt:[{$size:$marks}, ?0 ]}}")
	List<IdNamePhone> findByFewMarks(int thresholdMarks);
	/**************************************************************/
	@Query(value="{$and:[{marks: {$elemMatch:{subject: ?0,score:{$gte:?1}}}},"
			+ " {marks: {$not:{$elemMatch:{subject: ?0,score:{$lt:?1}}}}}]}")
	List<IdNamePhone> findByGoodMarksSubject(String subject, int thresholdScore);
	/**********************************************************************/
	@Query(value="{$expr:{$and:[{$gte: [ { $size: $marks }, ?0 ] }, {$lte:[{$size:$marks},?1]}]}}")
	List<IdNamePhone> findByRangeMarks(int min, int max);
	/****************************************************************/
	MarksOnly findByIdAndMarksSubject(long id, String subject);

}
