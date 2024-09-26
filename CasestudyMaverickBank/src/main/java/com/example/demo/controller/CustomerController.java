package com.example.demo.controller;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Account;
import com.example.demo.domain.Beneficiaries;
import com.example.demo.domain.Installments;
import com.example.demo.domain.Login;
import com.example.demo.domain.NewLoan;
import com.example.demo.domain.NewLoanList;
import com.example.demo.domain.User;
import com.example.demo.domain.Usershow;
import com.example.demo.repository.NewLoanListRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.ApprovalRequestService;
import com.example.demo.service.BankService;
import com.example.demo.service.BeneficiariesService;
import com.example.demo.service.InstallmentsService;
import com.example.demo.service.LoginService;
import com.example.demo.service.NewLoanService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthRequest;
import com.example.demo.util.JwtUtil;

@RestController
@RequestMapping("/customer")
@CrossOrigin("http://localhost:4200")
public class CustomerController {

	@Autowired
	private UserService service;
	
	@Autowired
	private LoginService lservice;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BankService bankService;
    
    @Autowired
    private BeneficiariesService benservice;
    
	@Autowired
    private NewLoanService newLoanService;
	
    @Autowired
    private NewLoanListRepository newLoanListRepository;
    
    @Autowired
    private InstallmentsService installmentservice;
    
    @Autowired
    private ApprovalRequestService appRequestService;
    

    
	//adding user, adding account and adding beneficiary
	@PostMapping(value = "/addUser")
	public void addUser(@RequestBody Map<String, Object> requestBody) throws ClassNotFoundException, SQLException {

		Map<String, Object> userMap = (Map<String, Object>) requestBody.get("users");
	    User user = new User();
	    user.setFirstname((String) userMap.get("firstname"));
	    user.setLastname((String) userMap.get("lastname"));

	    Date dateOfBirth = Date.valueOf((String) userMap.get("dateOfBirth")); 
	    user.setDateOfBirth(dateOfBirth);

	    user.setGender((String) userMap.get("gender"));
	    user.setContactNumber((String) userMap.get("contactNumber"));
	    user.setAddress((String) userMap.get("address"));
	    user.setCity((String) userMap.get("city"));
	    user.setState((String) userMap.get("state"));
	    user.setUsername((String) userMap.get("username"));
	    user.setEmailid((String) userMap.get("emailid"));

	    String password = (String) requestBody.get("password");
	    String role = (String) requestBody.get("role");

	    service.addUser(user);

	    Login login = new Login();
	    login.setUsername(user.getUsername());
	    login.setEmailid(user.getEmailid());
	    login.setPassword(password);
	    login.setRole(role);
	    
	    lservice.registerUser(login);
	}
	

    @PostMapping("/addAccounts")
    public Account addAccount(@RequestBody Map<String, Object> userdet) throws ClassNotFoundException, SQLException {
        Map<String, Object> accountdet = (Map<String, Object>) userdet.get("account");
        String aadhaarNumber = (String) userdet.get("aadhaarNumber");
        String panNumber = (String) userdet.get("panNumber");

        Double income = null;
        if (userdet.get("income") != null) {
            income = ((Number) userdet.get("income")).doubleValue(); 
        }
        
        Account account = new Account();
        account.setAccountType((String) accountdet.get("accountType"));
        account.setBalance(((Number) accountdet.get("balance")).doubleValue());
        account.setBranchName((String) accountdet.get("branchName"));
        account.setIfscCode((String) accountdet.get("ifscCode"));
        account.setUsername((String) accountdet.get("username"));
        account.setEmailid((String) accountdet.get("emailid"));

        return accountService.addAccount(account, aadhaarNumber, panNumber,income);
    }
    
    @PostMapping("/addBeneficiary")
    public String addBeneficiary(@RequestBody Beneficiaries benficiary) {
    	return benservice.giveBeneficiaryDetails(benficiary);
    }
    
    //making transactions
    @PutMapping("/deposit/{accountNumber}/{amount}")
    public String deposit(@PathVariable String accountNumber, @PathVariable double amount) throws ClassNotFoundException, SQLException {
        return bankService.deposit(accountNumber, amount);
    }

    @PutMapping("/withdraw/{accountNumber}/{amount}")
    public String withdraw(@PathVariable String accountNumber, @PathVariable double amount) throws ClassNotFoundException, SQLException {
        return bankService.withdraw(accountNumber, amount);
    }
 
    @PutMapping("/accounttransfer/{accnum1}/{accnum2}/{amount}")
    public String accountransfer(@PathVariable String accnum1, @PathVariable String accnum2, @PathVariable double amount) throws ClassNotFoundException, SQLException {
        return bankService.accountTransfer(accnum1, accnum2, amount);
    }
    
    @PutMapping("/transferbyPhone/{phnum1}/{phnum2}/{amount}")
    public String transferByPhone(@PathVariable String phnum1, @PathVariable String phnum2, @PathVariable double amount) throws ClassNotFoundException, SQLException {
        return bankService.transferByContactNumber(phnum1, phnum2, amount);
    }
	 
    //loanlist display and with that apply for loan
    @GetMapping("/loanlist")
    public List<NewLoanList> getAllLoanTypes() {
    	return newLoanListRepository.findAll();
	}
	    
    @PostMapping("/loans/apply")
	public String applyForLoan(@RequestParam String accountNumber,
			@RequestParam String loanName, @RequestParam double loanAmount,@RequestParam double income) {
	        
	    NewLoan newLoan = new NewLoan();
	    newLoan.setAccountNumber(accountNumber);
     	newLoan.setLoanName(loanName);
	    newLoan.setLoanAmount(loanAmount);
	    newLoan.setStatus("Pending"); 
	
	    NewLoan savedLoan = newLoanService.applyloan(newLoan,income);
	
       return "Loan application successful with ID: " + savedLoan.getLoanId();
      }
	
    //Paying installments
    
    @GetMapping("/loans/searchinstallments/{loanid}")
    public List<Installments>searchInstallments(@PathVariable int loanid){
    	return installmentservice.searchInstallments(loanid);
    }
    
    @PutMapping("/loans/makepayments")
    public String payInstallments(@RequestParam int loanId,
	    		@RequestParam String paymentDate,@RequestParam String paymentType,
	    		@RequestParam double paymentAmount,@RequestParam String remarks) {
	    return installmentservice.payInstallment(loanId, paymentDate, paymentType, paymentAmount, remarks);
	 }
	    
    //closing request for user account,bank account and loan
	@PostMapping("/close-useraccount-request/{username}")
	public String closerequest(@PathVariable String username) {
		return appRequestService.closeUserAccountsRequest(username);
	}
		 
	@PostMapping("/close-bankaccount-request/{accountNumber}")
	public String closebrequest(@PathVariable String accountNumber) {
		return appRequestService.closeBankAccountsRequest(accountNumber);
	}
	    
	@PostMapping("/close-loan-request/{loanid}")
    public String closeRequest (@PathVariable int loanid) {
		return appRequestService.LoanClose(loanid);
	}
	
	//standard methods
	@GetMapping("/searchBen/{username}")
    public List<Beneficiaries> searchBeneficiary(@PathVariable String username) {
		return benservice.searchBeneficiaries(username);
	}
	    
    @GetMapping("/myaccounts/{username}")
    public List<Account> getAccountByNumberByusername(@PathVariable String username) {
        return accountService.getAccountByNumberByusername(username);
    }
    
    @GetMapping(value="/searchUser/{username}")
	public Usershow searchuser(@PathVariable String username) {
		return service.searchUser(username);
	}
    
    
    @GetMapping("/custlogin/{username}/{password}")
	 public String custAuth(@PathVariable String username,@PathVariable String password) throws ClassNotFoundException, SQLException {
		return lservice.customerauthenticate(username, password);
	 }
		
	@GetMapping("/login/{username}/{password}")
	 public String loginuser(@PathVariable String username,@PathVariable String password) throws ClassNotFoundException, SQLException {
		return lservice.loginuser(username, password);
	 }
	

	
}
    
 
