package ma.ebanking.backend.web;

import ma.ebanking.backend.dto.*;
import ma.ebanking.backend.exceptions.BalanceNotSufficientException;
import ma.ebanking.backend.exceptions.BankAccountNotFoundException;
import ma.ebanking.backend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/debit/{accountId}")
    public DebitDTO debitByPath(@PathVariable String accountId,
                                @RequestParam("amount") double amount,
                                @RequestParam(name = "desc", required = false) String desc) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(accountId, amount, desc == null ? "" : desc);
        DebitDTO d = new DebitDTO();
        d.setAccountId(accountId);
        d.setAmount(amount);
        d.setDescription(desc);
        return d;
    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {
        this.bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/credit/{accountId}")
    public CreditDTO creditByPath(@PathVariable String accountId,
                                  @RequestParam("amount") double amount,
                                  @RequestParam(name = "desc", required = false) String desc) throws BankAccountNotFoundException {
        this.bankAccountService.credit(accountId, amount, desc == null ? "" : desc);
        CreditDTO c = new CreditDTO();
        c.setAccountId(accountId);
        c.setAmount(amount);
        c.setDescription(desc);
        return c;
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfer(
                transferRequestDTO.getAccountSource(),
                transferRequestDTO.getAccountDestination(),
                transferRequestDTO.getAmount());
    }
}
