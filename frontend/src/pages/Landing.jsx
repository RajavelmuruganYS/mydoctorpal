import Hero from "./landing/Hero";
import HowItWorks from "./landing/HowItWorks";
import StatsBar from "./landing/StatsBar";
import RoleShowcase from "./landing/RoleShowcase";
import WhyGrid from "./landing/WhyGrid";
import Testimonials from "./landing/Testimonials";
import CTASection from "./landing/CTASection";
import Footer from "./landing/Footer";

export default function Landing() {
  return (
    <div>
      <Hero />
      <HowItWorks />
      <StatsBar />
      <RoleShowcase />
      <WhyGrid />
      <Testimonials />
      <CTASection />
      <Footer />
    </div>
  );
}