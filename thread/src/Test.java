import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// При Dead Lock нужно использовать ReentrantLock, потому что в нем есть возможность освобождать мониторы 
public class Test {
    public static void main(String[] args) throws InterruptedException {
        Runner runner= new Runner();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                runner.firstThread();
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                runner.secondThread();

            }
        });
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        runner.finish();

    }
}
class Runner{
    private Account account1 = new Account();
    private Account account2 = new Account();
    private  Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();

    private  void takesLocks(Lock lock1, Lock lock2){
        boolean firstLockTaken = false;
        boolean secondLockTaken = false;
        while (true) {
            try {
                firstLockTaken = lock1.tryLock();      // создаем две булевские переменные в которых проверяем заняты ли локи
                secondLockTaken = lock2.tryLock();     //
            } finally {
                if (firstLockTaken && secondLockTaken) {  // если все локи свободны, возвращаемся из цикла
                    return;
                }
                if (firstLockTaken) {    // если занят первый лок, то освобождаем
                    lock1.unlock();
                }
                if (secondLockTaken) {   // если занят второй лок, то освобождаем и идем дальше
                    lock2.unlock();
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
}


    public void firstThread(){
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
                takesLocks(lock1,lock2);
            try { Account.transfer(account1, account2, random.nextInt(100));
            }finally {
                lock1.unlock();
                lock2.unlock();
            }

        }
    }
    public void secondThread(){
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
                takesLocks(lock2, lock1); // избегаем deadLock
            try { Account.transfer(account2, account1, random.nextInt(100));
            }finally {
                lock2.unlock();
                lock1.unlock();
            }

        }
    }
    public void finish(){
        System.out.println(account1.getBalance());
        System.out.println(account2.getBalance());
        System.out.println("Total balance: " + (account1.getBalance()+ account2.getBalance()));
    }
}
class Account{
    private int balance=10000;
    public void deposit(int amount){
        balance+=amount;
    }
    public void withdraw(int amount){
        balance-=amount;
    }

    public int getBalance() {
        return balance;
    }
    public static void transfer(Account account1, Account account2, int amount){
        account1.withdraw(amount);
        account2.deposit(amount);
    }
}
