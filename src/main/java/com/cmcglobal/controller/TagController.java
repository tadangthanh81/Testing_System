/**
 * Create by: User - CMC
 * Create date: Feb 11, 2019
 * Modifier: User
 * Modified date: Feb 11, 2019
 * Description: ....
 * Version 1.0
 */
package com.cmcglobal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cmcglobal.entity.QuestionTag;
import com.cmcglobal.service.TagService;

/**
 * Create by: thanhtd - CMC
 * Create date: Feb 11, 2019
 * Modifier: thanhtd
 * Modified date: Feb 11, 2019
 * Description: ....
 * Version 1.0
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge=3600)
public class TagController {

	@Autowired
	TagService tagService;
	
	@RequestMapping(value = "/tag", method = RequestMethod.GET)
	public List<QuestionTag> getAllTag(){
		return tagService.getAllTag();
	}
}
