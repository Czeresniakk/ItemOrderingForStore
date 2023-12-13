package com.ltp.globalsuperstore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StoreController 
{
  private List<Item> itemList = new ArrayList<>();

  @RequestMapping("/")  
    public String getForm(Model model, @RequestParam(required = false) String id) {
      model.addAttribute("categories", Constants.CATEGORIES);
      if(getItemIndex(id) == Constants.NOT_FOUND) {
        model.addAttribute("item", new Item());
      }else {
        model.addAttribute("item", itemList.get(getItemIndex(id)));
      }
      return "form" ;
    }
  
  @GetMapping("/inventory")
  public String getInventory(Model model){  
    model.addAttribute("itemList", itemList);

    return "inventory";
  }
  
  @PostMapping("/submitItem")
  public ModelAndView handleSubmit(Item item, RedirectAttributes redirectAttributes) {

    if(getItemIndex(item.getId()) == Constants.NOT_FOUND){
      redirectAttributes.addFlashAttribute("status", Constants.SUCCESS_STATUS);
      itemList.add(item);
    
    } else if(within5Days(item.getDate(), itemList.get(getItemIndex(item.getId())).getDate())) {
      itemList.set(getItemIndex(item.getId()), item);
   
    } else{
      redirectAttributes.addFlashAttribute("status", Constants.FAILED_STATUS);
    }

    return new ModelAndView("redirect:/inventory"); 
  }

  private int getItemIndex(String id)
  { 
    for (int i = 0; i < itemList.size(); i++) {
      if(itemList.get(i).getId().equals(id)) {
        return i;
      }
    }
    return Constants.NOT_FOUND;
  } 

    public boolean within5Days(Date newDate, Date oldDate) {
      long diff = Math.abs(newDate.getTime() - oldDate.getTime());
      return (int) (TimeUnit.MILLISECONDS.toDays(diff)) <= 5;
  }

} 
