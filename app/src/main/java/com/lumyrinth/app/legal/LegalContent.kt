package com.lumyrinth.app.legal

/**
 * Legal disclosures, privacy policy terms, and conditions for Lumyrinth.
 * 
 * ⚠️ NOTE FOR DEVELOPER (Yu):
 * Google Play requires a publicly accessible URL to this Privacy Policy in the Play Console listing
 * (App content → Privacy policy). Host the content of [LegalContent.PrivacyPolicy] on a public static page
 * (e.g. GitHub Pages or your custom domain) and update the contact placeholder email before store submission.
 */
object LegalContent {
    const val LAST_UPDATED = "August 25, 2026"
    const val SUPPORT_EMAIL = "support@yourdomain.com"

    data class LegalSection(
        val title: String,
        val paragraphs: List<String>,
        val bulletPoints: List<String> = emptyList(),
    )

    object PrivacyPolicy {
        const val TITLE = "Privacy Policy"
        const val SUMMARY = "Lumyrinth is designed from the ground up as a private, local-first application. All your data stays exclusively on your device and is never sent to our servers or any third party."

        val sections = listOf(
            LegalSection(
                title = "1. Overview & Core Commitment",
                paragraphs = listOf(
                    "Your privacy is fundamental to the mindfulness experience. Lumyrinth operates entirely on your local device. We do not operate remote user accounts, do not maintain central databases, and do not track or sell your personal activity.",
                    "All your data stays on your device and is never sent to us or any third party."
                ),
            ),
            LegalSection(
                title = "2. Data Stored Locally on Your Device",
                paragraphs = listOf(
                    "To provide guided breathing exercises, track your personal consistency, and remember your settings, Lumyrinth saves the following data strictly within Android's private app sandbox:",
                ),
                bulletPoints = listOf(
                    "User Preferences: Your selected mindfulness goals (e.g. relaxation, focus, sleep), default sound/haptic toggles, daily reminder time, and favorite rhythm IDs.",
                    "Custom Rhythms: Any breathing ratios you create (inhale, hold, exhale, hold2 durations and default session length).",
                    "Session History: Timestamps of completed or logged sessions, actual and planned mindful minutes, total breath cycles completed, and optional post-session mood check-ins."
                ),
            ),
            LegalSection(
                title = "3. Storage Location & Security",
                paragraphs = listOf(
                    "All data is saved locally using standard Android on-device storage (Jetpack DataStore and SQLite via Room Database).",
                    "Because your data never leaves your device, it is protected by your device's built-in operating system encryption and sandbox isolation. There is no cloud transmission, no remote synchronization, and no risk of server-side data breaches."
                ),
            ),
            LegalSection(
                title = "4. Device Permissions & Why We Need Them",
                paragraphs = listOf(
                    "Lumyrinth requests only the minimum permissions necessary to deliver its core features:",
                ),
                bulletPoints = listOf(
                    "Notifications (POST_NOTIFICATIONS): Used solely if you choose to enable the optional Daily Mindful Reminder in Settings. This permission is requested only when you toggle the reminder on. If denied, the toggle remains off and all other app features continue to function normally.",
                    "Vibration (VIBRATE): Used solely to provide gentle tactile pulses that signal inhalation, retention, and exhalation phase transitions during active breathing sessions.",
                    "Permissions We Never Request: We do not request location, camera, microphone, contacts, external storage, or background network tracking permissions."
                ),
            ),
            LegalSection(
                title = "5. Third-Party Services & Analytics",
                paragraphs = listOf(
                    "We do not use third-party analytics, diagnostic trackers, or advertising SDKs. No analytics beacons or advertising identifiers are ever transmitted from Lumyrinth."
                ),
            ),
            LegalSection(
                title = "6. How to Delete Your Data",
                paragraphs = listOf(
                    "You retain complete control over your data at all times. You can permanently erase all stored preferences, custom rhythms, and session history at any moment by navigating to Settings → About → 'Clear all my data'.",
                    "Alternatively, uninstalling Lumyrinth from your Android device immediately and permanently removes all sandboxed database records and preferences."
                ),
            ),
            LegalSection(
                title = "7. Contact Information",
                paragraphs = listOf(
                    "If you have any questions or feedback regarding this Privacy Policy or your data, please contact the developer at $SUPPORT_EMAIL."
                ),
            ),
        )
    }

    object TermsOfService {
        const val TITLE = "Terms & Conditions"
        const val SUMMARY = "Please read these terms carefully before using Lumyrinth. By accessing or using the application, you agree to be bound by these terms."

        val sections = listOf(
            LegalSection(
                title = "1. Acceptance of Terms",
                paragraphs = listOf(
                    "By opening, installing, or using the Lumyrinth application, you acknowledge that you have read, understood, and agreed to be bound by these Terms and Conditions and our Privacy Policy.",
                    "If you do not agree with any part of these terms, please discontinue use of the application."
                ),
            ),
            LegalSection(
                title = "2. Not Medical Advice Disclaimer",
                paragraphs = listOf(
                    "IMPORTANT HEALTH AND WELLNESS NOTICE:",
                    "Lumyrinth is designed solely as a digital wellness tool for general relaxation, self-care, and mindfulness breathing exercises. Lumyrinth is NOT a medical device, medical software, or clinical diagnostic instrument.",
                    "The application does NOT provide medical advice, diagnosis, treatment, therapy, or prevention for any medical, physical, respiratory, cardiac, psychiatric, or psychological illness or disorder.",
                    "Guided breathwork can produce physiological changes. If you have pre-existing medical conditions—including, but not limited to, asthma, chronic obstructive pulmonary disease (COPD), cardiovascular disease, epilepsy, severe anxiety or panic disorders, or if you are pregnant—you should consult a qualified physician or healthcare provider before engaging in breathwork exercises.",
                    "Always listen to your body. If at any time during a breathing exercise you experience dizziness, lightheadedness, shortness of breath, hyperventilation, chest tightness, or physical discomfort, stop immediately and resume normal breathing."
                ),
            ),
            LegalSection(
                title = "3. User-Generated Content & Ownership",
                paragraphs = listOf(
                    "Any custom breathing ratios, rhythm presets, or session notes that you create in Lumyrinth belong entirely to you.",
                    "These patterns remain stored strictly on your local device. Lumyrinth claims no copyright, intellectual property rights, or ownership over your custom creations."
                ),
            ),
            LegalSection(
                title = "4. License & Intellectual Property",
                paragraphs = listOf(
                    "Subject to your compliance with these terms, you are granted a personal, non-exclusive, non-transferable, revocable license to use Lumyrinth on your personal Android devices for personal, non-commercial purposes.",
                    "The visual artwork, celestial animations, audio compositions, UI design, and branding associated with Lumyrinth are protected by copyright and intellectual property laws."
                ),
            ),
            LegalSection(
                title = "5. Disclaimer of Warranties & Limitation of Liability",
                paragraphs = listOf(
                    "Lumyrinth is provided on an 'AS IS' and 'AS AVAILABLE' basis without warranties of any kind, whether express or implied.",
                    "To the maximum extent permitted by applicable law, the developer and creators of Lumyrinth shall not be liable for any direct, indirect, incidental, special, consequential, or punitive damages arising out of your access to or use of the application."
                ),
            ),
            LegalSection(
                title = "6. Changes to Terms",
                paragraphs = listOf(
                    "We reserve the right to modify or update these Terms & Conditions at any time. When changes occur, the 'Last Updated' date at the top of this document will reflect the revision date. Your continued use of the application following any modifications signifies your acceptance of the updated terms."
                ),
            ),
            LegalSection(
                title = "7. Contact",
                paragraphs = listOf(
                    "If you have inquiries regarding these Terms and Conditions, please contact us at $SUPPORT_EMAIL."
                ),
            ),
        )
    }
}
