package io.pivotal.syanagihara.blog.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController {

    @GetMapping("/blog")
    fun blog(model: Model): String {
        model["title"] = "Blog"
        return "blog"
    }
}
