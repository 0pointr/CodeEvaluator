What is it?
-----
A desktop based test case evaluator for code competitions, written in Java. (for C, C++; but easily adaptable to other langs)

Why is it?
------
Because manually checking and logging entries in locally organized code competitions is time consuming. PITA in short.

Features
--------
* Set test cases
* Set whether tc is visible / hidden
* Set timeout period
* Set compiler

How to use?
--------
1. Create test cases  
Format of a tc file is 
```
INPUT
{input lines}
OUTPUT
{output lines}
```
e.g.  
```
INPUT
1
4
2 2 3 7
OUTPUT
2
```
2. Add the tc files in configuration
[scrshot1](https://raw.githubusercontent.com/0pointr/CodeEvaluator/master/ScrShots/2016-08-30%2021_58_57-Configuration.png),
[scrshot2](https://raw.githubusercontent.com/0pointr/CodeEvaluator/master/ScrShots/2016-08-30%2021_59_53-Configuration.png)
3. Save config
4. Set the compiler and configuration file
[scrshot1](https://github.com/0pointr/CodeEvaluator/blob/master/ScrShots/2016-08-30%2022_00_29-Configuration.png)
5. Select solutoion file and run agains test cases
[scrshot1](https://raw.githubusercontent.com/0pointr/CodeEvaluator/master/ScrShots/2016-08-30%2022_01_21-CodeCombat%20Code%20Evaluator.png),
[scrshot2](https://raw.githubusercontent.com/0pointr/CodeEvaluator/master/ScrShots/2016-08-30%2022_04_07-CodeCombat%20Code%20Evaluator.png),
[scrshot3](https://raw.githubusercontent.com/0pointr/CodeEvaluator/master/ScrShots/2016-08-30%2022_04_59-CodeCombat%20Code%20Evaluator.png)

---
#### Can I copy/modify/distribute ?
Yes, of course, provided you keep the original copyright information intact.  

___
* Notes
  * Currently the compiled file name to be run is hard-coded as 'a.exe'. That is easily customizable in ExecutionManager.java
  * The ExecutionManager implementation executes by means of Runtime.getRuntime().exec(). This has some extra overheads and may result in logging more execution time that the solution actually took. This is possibly linear in amount of input/output. A better approach would be using the newer ProcessBuilder class.
  * Sometimes compilation of solution files has security problems in Win7. I have no idea why.