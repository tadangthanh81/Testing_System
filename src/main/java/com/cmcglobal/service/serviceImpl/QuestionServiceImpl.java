package com.cmcglobal.service.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cmcglobal.entity.Category;
import com.cmcglobal.entity.Question;
import com.cmcglobal.entity.QuestionLevel;
import com.cmcglobal.entity.QuestionTag;
import com.cmcglobal.entity.QuestionType;
import com.cmcglobal.repository.QuestionRepository;
import com.cmcglobal.service.CategoryService;
import com.cmcglobal.service.LevelService;
import com.cmcglobal.service.QuestionService;
import com.cmcglobal.service.TagService;
import com.cmcglobal.service.TypeSevice;

@Service
public class QuestionServiceImpl implements QuestionService {
	
	private static final Logger logger = Logger.getLogger(QuestionServiceImpl.class);

	@Autowired
	EntityManager entityManager;

	@Autowired
	TypeSevice typeService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	LevelService levelService;

	@Autowired
	TagService tagService;

	@Autowired
	QuestionRepository questionRepository;

	@Override
	public Question findById(String id) {
		try {
			logger.info("Find Question by id = " + id);
			return questionRepository.findById(id).get();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void insertQuestion(Question question) {
		try {
			
			questionRepository.save(question);
			logger.info("Insert question success");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void deletebyId(String id) {
		try {
			questionRepository.deleteById(id);
			logger.info("Delete question by id = " + id);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public String editQuestion(String id, Question newQuestion) {
		try {
			logger.info("Edit question by id = " + id);
			Boolean existQ = questionRepository.existsById(id);
			if (!existQ) {
				return "No question with id above";
			} else {
				newQuestion.setId(id);
				questionRepository.saveAndFlush(newQuestion);
				return "Update success";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "fail";
		}
	}

	@Override
	public String editQuestion1(Question newQuestion) {
		try {
			System.out.println(newQuestion.getId());
			questionRepository.saveAndFlush(newQuestion);
			return "Update success";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "fail";
		}
	}

	@Override
	public List<Question> searchByContent(String contentSearch, Pageable pageable) {
		try {
			logger.info("Search question by text:  " + contentSearch);
			return questionRepository.findByContentContaining(contentSearch, pageable);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<Question> pageQuestion(Pageable pageable) {
		try {
			return questionRepository.pageQuestion(pageable);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmcglobal.service.QuestionService#countQuestion()
	 */
	@Override
	public String countQuestion() {
		try {
			return questionRepository.questionSum();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "fail";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmcglobal.service.QuestionService#filterByTestString(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public List<Question> filterByAttribute(String categoryName, String levelName, String typeName, String fullName,
	        String tagName, Pageable pageable) {
		try {			
			return questionRepository.filterByAttribute(categoryName, levelName, typeName, fullName, tagName, pageable);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmcglobal.service.QuestionService#filterByAll(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public List<Question> filterByAll(String categoryName, String levelName, String typeName, String fullName,
	        java.util.Date dateCreated, String tagName, Pageable pageable) {

		try {
			return questionRepository.filterByAll(categoryName, levelName, typeName, fullName, dateCreated, tagName,
			        pageable);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmcglobal.service.QuestionService#countSearchQuestion(java.lang.String)
	 */
	@Override
	public String countSearchQuestion(String content) {
		try {
			content = "%" + content + "%";
			return questionRepository.countSearchQuestion(content);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * Yen Trinh (non-Javadoc)
	 * 
	 * @see com.cmcglobal.service.QuestionService#createId()
	 */
	@Override
	public List<Question> readExcel(final String exelFilePath){
		logger.info("Read Excel");
		final int COLUMN_INDEX_CATEGORYID = 0;
		final int COLUMN_INDEX_CONTENT = 1;
		final int COLUMN_INDEX_TYPEID = 2;
		final int COLUMN_INDEX_LEVELID = 3;
		final int COLUMN_INDEX_SUGGESTION = 4;
		final int COLUMN_INDEX_TAGID = 5;
		final int COLUMN_INDEX_STATUS = 6;

		final int COLUMN_INDEX_CREATED_BY = 7;

		final int COLUMN_INDEX_CREATED_AT = 8;
//	final int COLUMN_INDEX_ANSWER = 9;

		List<Question> listQuestion = new ArrayList<Question>();

		File file = new File(exelFilePath);
		try {
			FileInputStream fileInput = new FileInputStream(file);

			Workbook workbook = getWorkbook(fileInput, exelFilePath);
			Sheet sheet = workbook.getSheetAt(0);

//		Row rowFirst = sheet.getRow(0);
//		Iterator<Cell> cellrowFirst = rowFirst.cellIterator();

			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Question question = new Question();

				Row row = sheet.getRow(rowNum);
				if (row == null) {
					break;
				}
				Iterator<Cell> cellIt = row.cellIterator();
				while (cellIt.hasNext()) {
					Cell cell = cellIt.next();
					Object cellValue = getCellValue(cell);
					if (cellValue == null || cellValue.toString().isEmpty()) {
						continue;
					}
					System.out.println(cell.toString());

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					int columnIndex = cell.getColumnIndex();

					switch (columnIndex) {
					case COLUMN_INDEX_CATEGORYID:
						float catergoryCell = Float.parseFloat(getCellValue(cell).toString());
						int categoryId = (int) catergoryCell;
						Category category = categoryService.findById(categoryId);
						question.setQuestionCategory(category);
						System.out.println(category.toString());
						break;
					case COLUMN_INDEX_CONTENT:
						question.setContent((String) getCellValue(cell));
						break;
					case COLUMN_INDEX_TYPEID:
						float typeCell = Float.parseFloat(getCellValue(cell).toString());
						int typeId = (int) typeCell;
						QuestionType questionType = typeService.getOneById(typeId);
						question.setQuestionType(questionType);
						break;
					case COLUMN_INDEX_LEVELID:
						float levelCell = Float.parseFloat(getCellValue(cell).toString());
						int levelId = (int) levelCell;
						QuestionLevel questionLevel = levelService.getOneById(levelId);
						question.setQuestionLevel(questionLevel);
						break;

					case COLUMN_INDEX_SUGGESTION:
						question.setSugguestion((String) getCellValue(cell));
						break;

					case COLUMN_INDEX_TAGID:
						float tagCell = Float.parseFloat(getCellValue(cell).toString());
						int tagId = (int) tagCell;
						QuestionTag questionTag = tagService.getOneById(tagId);
						question.setQuestionTag(questionTag);
						break;
					case COLUMN_INDEX_STATUS:
						float x = Float.parseFloat(getCellValue(cell).toString());
						int status = (int) x;
						question.setStatus(status);
						break;

					case COLUMN_INDEX_CREATED_AT:
						try {

							question.setDateCreated(formatter.parse(getCellValue(cell).toString()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;
					case COLUMN_INDEX_CREATED_BY:

						break;
//				case COLUMN_INDEX_ANSWER:
//
//					break;

					default:
						break;
					}
				}
				listQuestion.add(question);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listQuestion;
	}

// Get Cell's value
	private static Object getCellValue(Cell cell) {
		
		logger.info("Get cell value");
		CellType cellType = cell.getCellTypeEnum();
		Object cellValue = null;
		switch (cellType) {
		case BOOLEAN:
			cellValue = cell.getBooleanCellValue();
			break;
		case FORMULA:
			Workbook workbook = cell.getSheet().getWorkbook();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			cellValue = evaluator.evaluate(cell).getNumberValue();
			break;
		case NUMERIC:
			cellValue = cell.getNumericCellValue();
			break;
		case STRING:
			cellValue = cell.getStringCellValue();
			break;
		case _NONE:
		case BLANK:
		case ERROR:
			break;
		default:
			break;
		}

		return cellValue;
	}

// Get Workbook
	private static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
		
		logger.info("Get work book");
		Workbook workbook = null;
		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmcglobal.service.QuestionService#createId()
	 */ // Yen Trinh
	@Override
	public String createId() {
		
		logger.info("Creat id");
		String id;
		List<Question> findAll = questionRepository.findAll();
		int ids = findAll.size() - 1;
		if (ids < 0) {
			ids = 0;
			return "Question001";
		} else {
			String tmp = findAll.get(ids).getId();
			tmp = tmp.substring(tmp.length() - 3, tmp.length());

			int id1 = Integer.parseInt(tmp) + 1;
			if (id1 < 10)
				id = ("Question00") + id1;
			else if (id1 > 9 && id1 < 100)
				id = ("Question0") + id1;
			else
				id = ("Question") + id1;
			return id;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmcglobal.service.QuestionService#findAll()
	 */
	@Override
	public List<Question> findAll() {
		try {
			logger.info("Get all question");
			return questionRepository.findAll();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
}
